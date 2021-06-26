package me.bryangaming.chatlab.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoginEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    public PlayerLoginEvent(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
