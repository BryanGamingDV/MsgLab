package me.bryangaming.chatlab.events;

import me.bryangaming.chatlab.data.UserData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SendDataEvent extends Event {

    private final Player player;
    private final UserData userData;

    private static final HandlerList HANDLERS = new HandlerList();

    public SendDataEvent(Player player, UserData userData) {
        this.player = player;
        this.userData = userData;
    }

    public Player getPlayer(){
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
