package me.bryangaming.chatlab.common.events.text;

import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent extends Event{

    private final String message;
    private final PlayerWrapper sender;
    private final UserData userData;

    private final AsyncPlayerChatEvent chatEvent;

    private static final HandlerList HANDLERS = new HandlerList();

    public ChatEvent(PlayerWrapper sender, UserData userData, AsyncPlayerChatEvent chatEvent, String message) {
        this.message = message;
        this.sender = sender;

        this.chatEvent = chatEvent;
        this.userData = userData;
    }

    public PlayerWrapper getSender() {
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
