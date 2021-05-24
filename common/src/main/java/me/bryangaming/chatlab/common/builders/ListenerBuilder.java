package me.bryangaming.chatlab.common.builders;

import me.bryangaming.chatlab.api.Listener;

public class ListenerBuilder {

    private final String event;
    private final Listener listener;

    private ListenerBuilder(String event, Listener listener){
        this.event = event;
        this.listener = listener;
    }

    public static ListenerBuilder create(String event, Listener listener){
        return new ListenerBuilder(event, listener);
    }

    public String getEventName(){
        return event;
    }

    public Listener getListener(){
        return listener;
    }
}
