package me.bryangaming.chatlab.api;

public interface Event {

    void setCancelled(boolean cancelled);

    boolean isCancelled();
}
