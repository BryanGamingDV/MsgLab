package me.bryangaming.chatlab.events.text;

import me.bryangaming.chatlab.data.UserData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent extends Event{

    private final String message;
    private final Player sender;
    private final UserData userData;

    private final AsyncPlayerChatEvent chatEvent;

    private static final HandlerList HANDLERS = new HandlerList();

    public ChatEvent(Player sender, UserData userData, AsyncPlayerChatEvent chatEvent, String message) {
        this.message = message;
        this.sender = sender;

        this.chatEvent = chatEvent;
        this.userData = userData;
    }

    public Player getSender() {
        return sender;
    }

    public UserData getUserData(){
        return userData;
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
