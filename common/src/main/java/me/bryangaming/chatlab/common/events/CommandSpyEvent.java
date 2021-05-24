package me.bryangaming.chatlab.common.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandSpyEvent extends  {

    private final String message;

    private final String sender;


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
