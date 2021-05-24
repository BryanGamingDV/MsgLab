package me.bryangaming.chatlab.common.events;


import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.listeners.command.SocialSpyListener;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SocialSpyEvent  {

    private String message;

    public SocialSpyEvent(PluginService pluginService, String message) {
        this.message = message;
        pluginService.getCache().getListeners
    }


}
