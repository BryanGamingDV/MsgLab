package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.CacheManager;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.server.ChangeMode;
import me.bryangaming.chatlab.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.managers.GroupMethod;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JoinListener implements Listener {

    private final PluginService pluginService;

    private final Configuration players;
    private final Configuration sounds;

    private final CacheManager cache;

    public JoinListener(PluginService pluginService) {
        this.pluginService = pluginService;
        // All methods:
        this.players = pluginService.getFiles().getPlayers();
        this.sounds = pluginService.getFiles().getSounds();
        this.cache = pluginService.getCache();
    }

    @EventHandler
    public boolean onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();

        ModuleCheck moduleCheck = pluginService.getPathManager();

        if (cache.getUserDatas().get(uuid) == null) {
            cache.getUserDatas().put(uuid, new UserData(uuid));
        }
        if (playerMethod.hasPermission(player, "commands.helpop.watch")) {
            cache.getUserDatas().get(uuid).toggleHelpOp(true);
        }
        String playerRank = groupMethod.getJQGroup(player);

        if (moduleCheck.isOptionEnabled("join_quit")) {
            if (!event.getPlayer().hasPlayedBefore()) {
                Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, event.getPlayer(), playerRank, ChangeMode.FIRST_JOIN));
            } else {
                Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, event.getPlayer(), playerRank, ChangeMode.JOIN));
            }
        }

        if (moduleCheck.isCommandEnabled("ignore")) {
            Map<UUID, List<String>> ignorelist = cache.getIgnorelist();
            List<String> playerlist = players.getStringList("players." + player.getUniqueId().toString() + ".players-ignored");

            if (!ignorelist.containsKey(uuid) && (!(playerlist.isEmpty()))) {
                ignorelist.put(uuid, playerlist);
            }
        }


        return true;
    }
}