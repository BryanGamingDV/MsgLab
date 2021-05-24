package me.bryangaming.chatlab.common.managers.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.utils.module.ModuleType;
import me.bryangaming.chatlab.common.wrapper.SenderWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.SenderWrapper;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

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

    public void ignorePlayer(SenderWrapper sender, UUID uuid) {

        PlayerWrapper you = (PlayerWrapper) sender;
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

    public void unignorePlayer(SenderWrapper sender, UUID uuid) {

        PlayerWrapper you = (PlayerWrapper) sender;
        UUID playeruuid = you.getUniqueId();

        PlayerWrapper target = ServerWrapper.getData().getPlayer(uuid);

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
        String playerName = ServerWrapper.getData().getPlayer(sender).getName();

        if (!(players.contains("players."))) return false;

        List<String> ignorelist = players
                .getStringList("players." + playerIgnored.toString() + ".players-ignored");


        return ignorelist.contains(playerName);
    }
}
