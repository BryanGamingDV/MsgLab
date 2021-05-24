package me.bryangaming.chatlab.common.events;

import me.bryangaming.chatlab.api.Event;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.listeners.command.HelpOpListener;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HelpOpEvent implements Event {

    private final String message;

    public HelpOpEvent(PluginService pluginService, String message) {
        this.message = message;
        pluginService.getCache().getListeners().get("HelpopEvent").doAction(this);
    }

    public String getMessage() {
        return message;
    }
}
