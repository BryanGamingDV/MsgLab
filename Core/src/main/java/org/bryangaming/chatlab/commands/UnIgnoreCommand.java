package org.bryangaming.chatlab.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import org.bryangaming.chatlab.managers.commands.IgnoreMethod;
import org.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bryangaming.chatlab.registry.ConfigManager;
import org.bryangaming.chatlab.utils.Configuration;
import org.bryangaming.chatlab.utils.module.ModuleCheck;
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
    public boolean onCommand(@Sender Player player, @OptArg OfflinePlayer target) {

        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        ModuleCheck moduleCheck = pluginService.getPathManager();

        ConfigManager files = pluginService.getFiles();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        if (target == null) {
            playerMethod.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("unignore", "<player>")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!(target.isOnline())) {
            playerMethod.sendMessage(player, messages.getString("error.player-offline"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(player.getName())) {
            playerMethod.sendMessage(player, messages.getString("error.ignore.ignore-yourself"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getName();
        UUID playeruuid = player.getUniqueId();

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();

        if (!(ignorelist.containsKey(playeruuid))) {
            playerMethod.sendMessage(player, messages.getString("error.ignore.anybody"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");
        UUID targetuuid = target.getPlayer().getUniqueId();

        if (!(ignoredlist.contains(targetname))) {
            playerMethod.sendMessage(player, messages.getString("error.ignore.already-unignored"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        ignoreMethod.unignorePlayer(player, targetuuid);
        playerMethod.sendMessage(player, command.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "ignore");
        return true;
    }
}
