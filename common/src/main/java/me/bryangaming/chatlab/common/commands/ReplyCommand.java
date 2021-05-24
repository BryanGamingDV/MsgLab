package me.bryangaming.chatlab.common.commands;

import me.bryangaming.chatlab.api.Listener;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.SocialSpyEvent;
import me.bryangaming.chatlab.common.loader.FileLoader;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.bryangaming.chatlab.common.wrapper.annotation.SenderAnnotWrapper;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.List;
import java.util.UUID;

public class ReplyCommand implements CommandClass {

    private final PluginService pluginService;

    public ReplyCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = {"reply", "r"})
    public boolean onCommand(@SenderAnnotWrapper PlayerWrapper sender, @OptArg("") @Text String text) {

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

        PlayerWrapper target = ServerWrapper.getData().getPlayer(playerCache.getRepliedPlayer());

        if (text.equalsIgnoreCase("-sender")) {
            senderManager.sendMessage(sender, commandFile.getString("commands.msg-reply.talked")
                    .replace("%player%", target.getName()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (commandFile.getBoolean("commands.msg-reply.enable-revisor")) {
            Listener<TextRevisorEvent> textrevisorEvent = new TextRevisorEvent(sender, text, TextRevisorEnum.TEXT);
            textrevisorEvent.doaction

            if (textrevisorEvent.isCancelled()){
                return true;
            }
        }

        if (!senderManager.hasPermission(sender, "color.variable")) {
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


