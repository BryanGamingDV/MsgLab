package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UnIgnoreCommand implements CommandClass {


    private final PluginService pluginService;

    public UnIgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;

    }

    @Command(names = "unignore")
    public boolean onCommand(@Sender Player sender, @OptArg OfflinePlayer target) {

        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreManager();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = pluginService.getFiles().getPlayersFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("unignore", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(senderManager.isOnline(sender))) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (senderManager.isVanished(sender)){
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            senderManager.sendMessage(sender, messagesFile.getString("ignore.error.ignore-yourself"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getName();
        UUID playeruuid = sender.getUniqueId();

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();

        if (!(ignorelist.containsKey(playeruuid))) {
            senderManager.sendMessage(sender, messagesFile.getString("ignore.error.anybody"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> ignoredPlayers = playersFile.getStringList("players." + playeruuid + ".players-ignored");
        UUID targetUniqueId = target.getPlayer().getUniqueId();

        if (!(ignoredPlayers.contains(targetname))) {
            senderManager.sendMessage(sender, messagesFile.getString("ignore.error.already-unignored"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        ignoreManager.unIgnorePlayer(sender, targetUniqueId);
        senderManager.sendMessage(sender, messagesFile.getString("ignore.player-unignored")
                .replace("%player%", targetname));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "unignore");
        return true;
    }
}
