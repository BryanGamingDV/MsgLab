package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
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

    public void ignorePlayer(Player sender, UUID targetUniqueID) {

        UUID senderUniqueID = sender.getUniqueId();

        Player target = Bukkit.getPlayer(targetUniqueID);
        List<String> ignoredPlayers;

        if (ignorelist.get(senderUniqueID) == null) {
            ignoredPlayers = new ArrayList<>();
        } else {
            ignoredPlayers = ignorelist.get(senderUniqueID);
        }

        ignoredPlayers.add(target.getName());
        ignorelist.put(senderUniqueID, ignoredPlayers);

        playerFile.set("players." + senderUniqueID + ".name", sender.getName());
        playerFile.set("players." + senderUniqueID + ".players-ignored", ignoredPlayers);

        playerFile.save();

    }

    public void unIgnorePlayer(Player sender, UUID uuid) {

        UUID senderUniqueId = sender.getUniqueId();
        Player target = Bukkit.getPlayer(uuid);

        List<String> ignoredPlayers = ignorelist.get(senderUniqueId);
        ignoredPlayers.remove(target.getName());

        playerFile.set("players." + senderUniqueId + ".players-ignored", ignoredPlayers);
        playerFile.save();

        if (playerFile.getStringList("players." + senderUniqueId + ".players-ignored").isEmpty()) {
            playerFile.set("players." + uuid, null);
            playerFile.save();
        }

    }

    public boolean playerIsIgnored(UUID sender, UUID playerIgnored) {

        if (!pluginService.getFiles().getConfigFile().getBoolean("modules.ignore.enabled")){
            return false;
        }

        Configuration playersFile = pluginService.getFiles().getPlayersFile();
        String playerName = Bukkit.getPlayer(sender).getName();

        if (!(playersFile.contains("players."))) return false;

        List<String> ignoredPlayers = playersFile
                .getStringList("players." + playerIgnored.toString() + ".players-ignored");


        return ignoredPlayers.contains(playerName);
    }
}
