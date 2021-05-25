package me.bryangaming.chatlab.spigot.impl;

import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.wrapper.OfflinePlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OfflinePlayerWrapperImpl
        implements OfflinePlayerWrapper{

    private final OfflinePlayer offlinePlayer;

    public OfflinePlayerWrapperImpl(OfflinePlayer player) {
        this.offlinePlayer = player;
    }

    @Override
    public UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

    @Override
    public String getName() {
        return offlinePlayer.getName();
    }

    @Override
    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    @Override
    public PlayerWrapper getPlayer(){
        return ServerWrapper.getData().getPlayer(offlinePlayer.getUniqueId());
    }
}
