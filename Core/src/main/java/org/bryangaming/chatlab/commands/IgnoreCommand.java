package org.bryangaming.chatlab.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import org.bryangaming.chatlab.bukkitutils.sound.SoundManager;
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

public class IgnoreCommand implements CommandClass {

    private final PluginService pluginService;

    public IgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = "ignore")
    public boolean onIgnoreCommand(@Sender Player sender, @OptArg OfflinePlayer target) {

        ConfigManager files = pluginService.getFiles();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        SoundManager sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (target == null) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("ignore", "<sender>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();
        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (target.getName().equalsIgnoreCase("-list")) {

            if (ignorelist.containsKey(playeruuid) || ignoredlist.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.ignore.anybody"));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.ignore.list-ignoredplayers"));
            for (String playersignored : ignoredlist) {
                playerMethod.sendMessage(sender, "&8- &a" + playersignored);
            }

            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!target.isOnline()) {
            playerMethod.sendMessage(sender, messages.getString("error.player-offline"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }


        if (target.getName().equalsIgnoreCase(sender.getName())) {
            playerMethod.sendMessage(sender, messages.getString("error.ignore.ignore-yourself"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getPlayer().getName();
        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();

        if (!ignorelist.containsKey(playeruuid)) {
            ignoreMethod.ignorePlayer(sender, target.getUniqueId());
            playerMethod.sendMessage(sender, command.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!(ignoredlist.contains(targetname))) {
            ignoreMethod.ignorePlayer(sender, target.getUniqueId());
            playerMethod.sendMessage(sender, command.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        ignoreMethod.unignorePlayer(sender, target.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
        return true;


    }


}
