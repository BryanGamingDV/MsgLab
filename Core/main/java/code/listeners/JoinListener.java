package code.listeners;

import code.CacheManager;
import code.PluginService;
import code.data.UserData;
import code.methods.ListenerManaging;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
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

        ListenerManaging listenerManaging = pluginService.getPlayerMethods().getListenerManaging();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        ModuleCheck moduleCheck = pluginService.getPathManager();

        if (moduleCheck.isOptionEnabled("join_quit")){
            listenerManaging.setJoin(event);
        }

        if (cache.getPlayerUUID().get(uuid) == null){
            cache.getPlayerUUID().put(uuid, new UserData(uuid));
        }

        if (playerMethod.hasPermission(player, "commands.helpop.watch")){
            cache.getPlayerUUID().get(uuid).toggleHelpOp(true);
        }

        if (moduleCheck.isCommandEnabled("ignore")){
            Map<UUID, List<String>> ignorelist = cache.getIgnorelist();
            List<String> playerlist = players.getStringList("players." + player.getUniqueId().toString() + ".players-ignored");

            if (!ignorelist.containsKey(uuid) && (!(playerlist.isEmpty()))) {
                ignorelist.put(uuid, playerlist);
            }
        }

        return true;
    }
}
