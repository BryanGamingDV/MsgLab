package me.bryangaming.chatlab.events.text;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent extends Event{

    private final String message;
    private final Player sender;

    private final AsyncPlayerChatEvent chatEvent;

    private static final HandlerList HANDLERS = new HandlerList();

    public ChatEvent(Player sender, AsyncPlayerChatEvent chatEvent, String message) {
        this.message = message;
        this.sender = sender;

        this.chatEvent = chatEvent;
    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public AsyncPlayerChatEvent getChatEvent(){
        return chatEvent;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
