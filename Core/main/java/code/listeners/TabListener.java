package code.listeners;

import code.PluginService;
import code.methods.FitlerMethod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class TabListener implements Listener {

    private PluginService pluginService;

    public TabListener(PluginService pluginService){
        this.pluginService = pluginService;
    }


    @EventHandler
    public void onTab(PlayerCommandSendEvent playerCommandSendEvent){
        FitlerMethod fitlerMethod = pluginService.getPlayerMethods().getFitlerMethod();

        fitlerMethod.onTab(playerCommandSendEvent);
    }
}
