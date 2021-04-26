package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.registry.FileLoader;
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

public class IgnoreCommand implements CommandClass {

    private final PluginService pluginService;

    public IgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = "ignore")
    public boolean onIgnoreCommand(@Sender Player sender, @OptArg OfflinePlayer target) {

        FileLoader files = pluginService.getFiles();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (target == null) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("ignore", "<sender>")));
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
        IgnoreManager ignoreManager = pluginService.getPlayerMethods().getIgnoreMethod();

        if (!ignorelist.containsKey(playeruuid)) {
            ignoreManager.ignorePlayer(sender, target.getUniqueId());
            playerMethod.sendMessage(sender, command.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!(ignoredlist.contains(targetname))) {
            ignoreManager.ignorePlayer(sender, target.getUniqueId());
            playerMethod.sendMessage(sender, command.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        ignoreManager.unignorePlayer(sender, target.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "ignore");
        return true;


    }


}
