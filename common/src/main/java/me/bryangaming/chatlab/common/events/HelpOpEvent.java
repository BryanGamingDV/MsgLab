package me.bryangaming.chatlab.common.events;

import me.bryangaming.chatlab.api.Event;

public class HelpOpEvent implements Event {

    private final String message;
    private boolean isCancelled = false;

    public HelpOpEvent(String message) {
        this.message = message;
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
