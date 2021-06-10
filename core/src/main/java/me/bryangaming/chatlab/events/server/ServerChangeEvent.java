package me.bryangaming.chatlab.events.server;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ServerChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Event event;
    private final Player sender;
    private final ChangeMode changeMode;
    private final String playerGroup;

    private final UUID uuid;

    public ServerChangeEvent(Event event, Player sender, String playerGroup, ChangeMode changeMode) {
        this.sender = sender;
        this.changeMode = changeMode;
        this.uuid = sender.getUniqueId();
        this.playerGroup = playerGroup;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public String getPlayerGroup() {
        return playerGroup;
    }

    public Player getPlayer() {
        return sender;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public ChangeMode getChangeMode() {
        return changeMode;
    }

    public void kickPlayer(String reason) {
        sender.kickPlayer(reason);
    }


    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
