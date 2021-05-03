package me.bryangaming.chatlab.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandSpyEvent extends Event {

    private final String message;

    private final String sender;

    private static final HandlerList HANDLERS = new HandlerList();

    public CommandSpyEvent(String sender, String message) {
        this.message = message;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
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
