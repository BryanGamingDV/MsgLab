package me.bryangaming.chatlab.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HelpOpEvent extends Event {

    private final String message;

    private static final HandlerList HANDLERS = new HandlerList();

    public HelpOpEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
