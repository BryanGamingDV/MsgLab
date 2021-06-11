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
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String senderMessage) {

        FileLoader files = pluginService.getFiles();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = files.getPlayersFile();

        Configuration configFile = files.getConfigFile();
        Configuration messagesFile = files.getMessagesFile();

        if (senderMessage.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("reply", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData senderData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!senderData.hasRepliedPlayer()) {
            if (!senderData.getRepliedBungeePlayer().isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.error.no-reply"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }
        }

        String targetName;
        if (!senderData.getRepliedBungeePlayer().isEmpty()) {
            targetName = Bukkit.getPlayer(senderData.getRepliedPlayer()).getName();
        } else {
            targetName = senderData.getRepliedBungeePlayer();
        }
        if (senderMessage.equalsIgnoreCase("-sender")) {
            senderManager.sendMessage(sender, messagesFile.getString("msg-reply.format.talked")
                    .replace("%player%", targetName));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (configFile.getBoolean("modules.msg-reply.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, senderMessage, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()) {
                return true;
            }
        }

        if (!senderManager.hasPermission(sender, "chat-format", "color")) {
            senderMessage = "<pre>" + senderMessage + "</pre>";
        }

        senderManager.sendMessage(sender, messagesFile.getString("msg-reply.format.player")
                        .replace("%player%", sender.getName())
                        .replace("%target%", targetName)
                , senderMessage);
        senderManager.playSound(sender, SoundEnum.RECEIVE_MSG);

        UUID senderUniqueId = sender.getUniqueId();

        List<String> ignoredPlayers = playersFile.getStringList("players." + senderUniqueId + ".players-ignored");

        if (!(ignoredPlayers.contains(targetName))){
            if (senderData.getRepliedBungeePlayer().isEmpty()) {
                pluginService.getRedisConnection().sendMessage("chatlab", MessageType.MSG, targetName, senderMessage);
                pluginService.getRedisConnection().sendMessage("chatlab", MessageType.REPLY, targetName, sender.getName());
            } else {
                senderManager.sendMessage(Bukkit.getPlayer(targetName), messagesFile.getString("msg-reply.format.player")
                                .replace("%player%", sender.getName())
                                .replace("%target%", targetName)
                        , senderMessage);

                UserData targetData = pluginService.getCache().getUserDatas().get(Bukkit.getPlayer(targetName).getUniqueId());
                targetData.setRepliedPlayer(senderUniqueId);
            }
            senderManager.playSound(sender, SoundEnum.RECEIVE_MSG);

        }

        String socialspyFormat = messagesFile.getString("socialspy.spy")
                .replace("%player%", sender.getName())
                .replace("%arg-1%", targetName)
                .replace("%message%", senderMessage);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));
        return true;
    }
}


