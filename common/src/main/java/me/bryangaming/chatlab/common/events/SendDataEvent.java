package me.bryangaming.chatlab.common.events;

import me.bryangaming.chatlab.api.Event;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.listeners.SendDataListener;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public class SendDataEvent implements Event {

    private final PlayerWrapper player;
    private final UserData userData;


    public SendDataEvent(PluginService pluginService, PlayerWrapper player, UserData userData) {
        this.player = player;
        this.userData = userData;
        pluginService.getCache().getListeners().get("SendDataEvent").doAction(this);
    }

    public PlayerWrapper getPlayer(){
        return player;
    }

    public UserData getUserData() {
        return userData;
    }
}
