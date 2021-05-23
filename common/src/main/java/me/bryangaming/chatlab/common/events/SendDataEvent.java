package me.bryangaming.chatlab.common.events;

import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SendDataEvent extends Event {

    private final PlayerWrapper player;
    private final UserData userData;

    private static final HandlerList HANDLERS = new HandlerList();

    public SendDataEvent(PlayerWrapper player, UserData userData) {
        this.player = player;
        this.userData = userData;
    }

    public PlayerWrapper getPlayer(){
        return player;
    }

    public UserData getUserData() {
        return userData;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
