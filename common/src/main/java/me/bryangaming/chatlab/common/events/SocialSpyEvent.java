package me.bryangaming.chatlab.common.events;


import me.bryangaming.chatlab.api.Event;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.listeners.command.SocialSpyListener;

public class SocialSpyEvent implements Event {

    private String message;
    private boolean isCancelled = false;


    public SocialSpyEvent(String message) {
        this.message = message;
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
