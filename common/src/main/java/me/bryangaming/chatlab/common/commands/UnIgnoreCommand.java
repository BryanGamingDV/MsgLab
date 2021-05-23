package me.bryangaming.chatlab.common.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.bryangaming.chatlab.common.wrapper.annotation.SenderAnnotWrapper;
import org.bukkit.OfflinePlayer;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UnIgnoreCommand implements CommandClass {


    private final PluginService pluginService;

    public UnIgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;

    }

    @Command(names = "unignore")
    public boolean onCommand(@SenderAnnotWrapper  PlayerWrapper sender, @OptArg OfflinePlayer target) {

        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = pluginService.getFiles().getPlayersFile();
        Configuration commandFile = pluginService.getFiles().getCommandFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("unignore", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(target.isOnline())) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(target)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            senderManager.sendMessage(sender, messagesFile.getString("error.ignore.ignore-yourself"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getName();
        UUID playeruuid = sender.getUniqueId();

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();

        if (!(ignorelist.containsKey(playeruuid))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.ignore.anybody"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> ignoredlist = playersFile.getStringList("players." + playeruuid + ".players-ignored");
        UUID targetuuid = target.getPlayer().getUniqueId();

        if (!(ignoredlist.contains(targetname))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.ignore.already-unignored"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        ignoreManager.unignorePlayer(sender, targetuuid);
        senderManager.sendMessage(sender, commandFile.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
        return true;
    }
}
