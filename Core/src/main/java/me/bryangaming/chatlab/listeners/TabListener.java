package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.FilterManager;
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
        FilterManager filterManager = pluginService.getPlayerManager().getFitlerMethod();
        filterManager.onTab(playerCommandSendEvent);
    }
}
