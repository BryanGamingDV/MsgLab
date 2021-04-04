package atogesputo.bryangaming.chatlab.listeners;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.managers.FitlerMethod;
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
        FitlerMethod fitlerMethod = pluginService.getPlayerMethods().getFitlerMethod();


        fitlerMethod.onTab(playerCommandSendEvent);
    }
}
