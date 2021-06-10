package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.SocialSpyEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.loader.FileLoader;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.redis.MessageType;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ReplyCommand implements CommandClass {

    private final PluginService pluginService;

    public ReplyCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = {"reply", "r"})
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String text) {

        FileLoader files = pluginService.getFiles();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = files.getPlayersFile();

        Configuration configFile = files.getConfigFile();
        Configuration messagesFile = files.getMessagesFile();


        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("reply", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData playerCache = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!playerCache.hasRepliedPlayer()) {
            if (!playerCache.getRepliedBungeePlayer().isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.error.no-reply"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }
        }

        String target;
        if (!playerCache.getRepliedBungeePlayer().isEmpty()) {
            target = Bukkit.getPlayer(playerCache.getRepliedPlayer()).getName();
        } else {
            target = playerCache.getRepliedBungeePlayer();
        }
        if (text.equalsIgnoreCase("-sender")) {
            senderManager.sendMessage(sender, messagesFile.getString("msg-reply.format.talked")
                    .replace("%player%", target));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (configFile.getBoolean("modules.msg-reply.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, text, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()) {
                return true;
            }
        }

        if (!senderManager.hasPermission(sender, "chat-format", "color")) {
            text = "<pre>" + text + "</pre>";
        }

        senderManager.sendMessage(sender, messagesFile.getString("msg-reply.format.player")
                        .replace("%player%", sender.getName())
                        .replace("%target%", target)
                , text);
        senderManager.playSound(sender, SoundEnum.RECEIVE_MSG);

        UUID playeruuid = sender.getUniqueId();

        List<String> ignoredlist = playersFile.getStringList("players." + playeruuid + ".players-ignored");

        if (!(ignoredlist.contains(target))){
            if (playerCache.getRepliedBungeePlayer().isEmpty()) {
                pluginService.getRedisConnection().sendMessage("chatlab", MessageType.MSG, target, text);
                pluginService.getRedisConnection().sendMessage("chatlab", MessageType.REPLY, target, sender.getName());
            } else {
                senderManager.sendMessage(Bukkit.getPlayerExact(target), messagesFile.getString("msg-reply.format.player")
                                .replace("%player%", sender.getName())
                                .replace("%target%", target)
                        , text);

                UserData targetCache = pluginService.getCache().getUserDatas().get(Bukkit.getPlayer(target).getUniqueId());
                targetCache.setRepliedPlayer(playeruuid);
            }
            senderManager.playSound(sender, SoundEnum.RECEIVE_MSG);

        }

        String socialspyFormat = messagesFile.getString("socialspy.spy")
                .replace("%player%", sender.getName())
                .replace("%arg-1%", target)
                .replace("%message%", text);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));
        return true;
    }
}


