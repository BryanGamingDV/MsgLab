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
    public boolean onCommand(@Sender Player player, @OptArg("") OfflinePlayer target) {

        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = pluginService.getFiles().getPlayersFile();
        Configuration commandFile = pluginService.getFiles().getCommandFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        if (target.getName().isEmpty()) {
            senderManager.sendMessage(player, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("unignore", "<player>")));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!(target.isOnline())) {
            senderManager.sendMessage(player, messagesFile.getString("error.player-offline"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(player.getName())) {
            senderManager.sendMessage(player, messagesFile.getString("error.ignore.ignore-yourself"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getName();
        UUID playeruuid = player.getUniqueId();

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();

        if (!(ignorelist.containsKey(playeruuid))) {
            senderManager.sendMessage(player, messagesFile.getString("error.ignore.anybody"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        List<String> ignoredlist = playersFile.getStringList("players." + playeruuid + ".players-ignored");
        UUID targetuuid = target.getPlayer().getUniqueId();

        if (!(ignoredlist.contains(targetname))) {
            senderManager.sendMessage(player, messagesFile.getString("error.ignore.already-unignored"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        ignoreManager.unignorePlayer(player, targetuuid);
        senderManager.sendMessage(player, commandFile.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "ignore");
        return true;
    }
}
