package me.bryangaming.chatlab.events.revisor;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextRevisorEvent extends Event implements Cancellable {

    private final Player player;
    private final String message;
    private final TextRevisorEnum revisorType;

    private String messageRevised = "";

    private final List<String> revisorExcpList;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public TextRevisorEvent(Player player, String message, TextRevisorEnum revisorType){
        this(player, message, revisorType, "");
    }

    public TextRevisorEvent(Player player, String message, TextRevisorEnum revisorType, String... revisorExcepcions) {
        this.message = message;
        this.player = player;
        this.revisorType = revisorType;
        if (!revisorExcepcions[0].isEmpty()) {
            this.revisorExcpList = Arrays.asList(revisorExcepcions);
        }else{
            this.revisorExcpList = new ArrayList<>();
        }
        this.isCancelled = false;

    }

    public List<String> getRevisorExcepcions() {
        return revisorExcpList;

    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public TextRevisorEnum getRevisorType(){
        return revisorType;
    }

    public String getMessageRevised(){
        return messageRevised;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public void setMessageRevised(String message){
        this.messageRevised = message;
    }
}
