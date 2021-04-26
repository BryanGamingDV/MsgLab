package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.FitlerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class TabListener implements Listener {

    private PluginService pluginService;

    public TabListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    @EventHandler
    public void onTab(PlayerCommandSendEvent playerCommandSendEvent) {
        FitlerManager fitlerManager = pluginService.getPlayerManager().getFitlerMethod();
        fitlerManager.onTab(playerCommandSendEvent);
    }
}
