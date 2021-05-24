package me.bryangaming.chatlab.common.listeners;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.events.server.ChangeMode;
import me.bryangaming.chatlab.common.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.common.managers.group.GroupManager;
import me.bryangaming.chatlab.common.utils.module.ModuleType;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.SendDataEvent;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;


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

        PlayerWrapper player = event.getPlayer();
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
