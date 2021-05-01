package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreManager {

    private final Configuration playerFile;

    private final PluginService pluginService;

    private final Map<UUID, List<String>> ignorelist;


    public IgnoreManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerFile = pluginService.getFiles().getPlayersFile();
        this.ignorelist = pluginService.getCache().getIgnorelist();
    }

    public void ignorePlayer(CommandSender sender, UUID uuid) {

        Player you = (Player) sender;
        UUID playeruuid = you.getUniqueId();

        OfflinePlayer player = Bukkit.getPlayer(uuid);
        List<String> ignoredPlayers;

        if (ignorelist.get(playeruuid) == null) {
            ignoredPlayers = new ArrayList<>();
        } else {
            ignoredPlayers = ignorelist.get(playeruuid);
        }

        ignoredPlayers.add(player.getName());
        ignorelist.put(playeruuid, ignoredPlayers);

        playerFile.set("players." + playeruuid + ".name", you.getName());
        playerFile.set("players." + playeruuid + ".players-ignored", ignoredPlayers);

        playerFile.save();

    }

    public void unignorePlayer(CommandSender sender, UUID uuid) {

        Player you = (Player) sender;
        UUID playeruuid = you.getUniqueId();

        OfflinePlayer target = Bukkit.getPlayer(uuid);

        List<String> ignoredPlayers = ignorelist.get(playeruuid);
        ignoredPlayers.remove(target.getName());
        playerFile.set("players." + playeruuid + ".players-ignored", ignoredPlayers);
        playerFile.save();

        if (playerFile.getStringList("players." + playeruuid + ".players-ignored").isEmpty()) {
            playerFile.set("players." + uuid, null);
            playerFile.save();
        }

    }

    public boolean playerIsIgnored(UUID sender, UUID playerIgnored) {

        if (!pluginService.getListManager().isEnabledOption(ModuleType.COMMAND, "ignore")) {
            return false;
        }

        Configuration players = pluginService.getFiles().getPlayersFile();
        String playerName = Bukkit.getPlayer(playerIgnored).getName();

        if (!(players.contains("players"))) return false;

        List<String> ignorelist = players
                .getStringList("players." + sender.toString() + ".players-ignored");

        return ignorelist.contains(playerName);
    }
}
