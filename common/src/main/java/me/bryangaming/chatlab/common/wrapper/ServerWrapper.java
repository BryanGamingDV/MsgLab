package me.bryangaming.chatlab.common.wrapper;

import me.bryangaming.chatlab.api.Event;

import java.util.List;
import java.util.UUID;

public interface ServerWrapper {

    static ServerWrapper getData(){
        return null;
    }

    int getOnlineSize();

    List<PlayerWrapper> getOnlinePlayers();

    boolean isPluginEnabled(String plugin);

    PlayerWrapper getPlayer(String name);

    PlayerWrapper getPlayer(UUID uuid);

    void callEvent(Event event);
}
