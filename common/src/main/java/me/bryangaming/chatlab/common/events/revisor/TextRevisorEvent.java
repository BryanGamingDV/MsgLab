package me.bryangaming.chatlab.common.events.revisor;

import me.bryangaming.chatlab.api.Event;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextRevisorEvent implements Event {

    private final PlayerWrapper player;
    private final String message;
    private final TextRevisorEnum revisorType;

    private String messageRevised = "";

    private final List<String> revisorExcpList;
    private boolean isCancelled;


    public TextRevisorEvent(PlayerWrapper player, String message, TextRevisorEnum revisorType){
        this(player, message, revisorType, "");
    }

    public TextRevisorEvent(PlayerWrapper player, String message, TextRevisorEnum revisorType, String... revisorExcepcions) {
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

    public PlayerWrapper getPlayer() {
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


    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public void setMessageRevised(String message){
        this.messageRevised = message;
    }
}
