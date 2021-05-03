package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.SocialSpyEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.loader.FileLoader;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
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
        Configuration commandFile = files.getCommandFile();
        Configuration messagesFile = files.getMessagesFile();

        UUID playeruuid = sender.getUniqueId();

        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("reply", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData playerCache = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!playerCache.hasRepliedPlayer()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-reply"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player target = Bukkit.getPlayer(playerCache.getRepliedPlayer());

        if (text.equalsIgnoreCase("-sender")) {
            senderManager.sendMessage(sender, commandFile.getString("commands.msg-reply.talked")
                    .replace("%player%", target.getName()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (commandFile.getBoolean("commands.msg-reply.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, text, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
                return true;
            }
        }

        if (!senderManager.hasPermission(sender, "color.commands")) {
            text = "<pre>" + text + "</pre>";
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.msg-reply.player")
                        .replace("%player%", sender.getName())
                        .replace("%arg-1%", target.getName())
                , text);
        senderManager.playSound(sender, SoundEnum.RECEIVE_MSG);

        List<String> ignoredlist = playersFile.getStringList("players." + playeruuid + ".players-ignored");

        if (!(ignoredlist.contains(target.getName()))) {
            senderManager.sendMessage(target, commandFile.getString("commands.msg-reply.player")
                            .replace("%player%", sender.getName())
                            .replace("%arg-1%", target.getName())
                    , text);

            UserData targetCache = pluginService.getCache().getUserDatas().get(target.getUniqueId());

            targetCache.setRepliedPlayer(playeruuid);
            senderManager.playSound(sender, SoundEnum.RECEIVE_MSG);
        }

        String socialspyFormat = commandFile.getString("commands.socialspy.spy")
                .replace("%player%", sender.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", text);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));
        return true;
    }
}


