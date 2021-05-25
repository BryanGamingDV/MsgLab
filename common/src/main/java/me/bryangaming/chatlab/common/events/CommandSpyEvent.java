package me.bryangaming.chatlab.common.events;

import me.bryangaming.chatlab.api.Event;

public class CommandSpyEvent implements Event {

    private final String message;
    private final String sender;
    private boolean isCancelled = false;

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


    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
