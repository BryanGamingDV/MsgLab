package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.SendDataEvent;
import me.bryangaming.chatlab.events.server.ChangeMode;
import me.bryangaming.chatlab.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.module.ModuleType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private final PluginService pluginService;


    public JoinListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public boolean onJoin(PlayerJoinEvent event) {

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        String playerRank = groupManager.getJQGroup(player);


        if (pluginService.getCache().getUserDatas().get(playerUUID) == null){
            pluginService.getCache().getUserDatas().put(playerUUID, new UserData(playerUUID));
        }

        UserData userData = pluginService.getCache().getUserDatas().get(playerUUID);

        Bukkit.getPluginManager().callEvent(new SendDataEvent(player, userData));

        if (pluginService.getListManager().isEnabledOption(ModuleType.MODULE, "join_quit")) {
            if (!event.getPlayer().hasPlayedBefore()) {
                Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, player, playerRank, ChangeMode.FIRST_JOIN));
            } else {
                Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, player, playerRank, ChangeMode.JOIN));
            }
        }

        return true;
    }
}
