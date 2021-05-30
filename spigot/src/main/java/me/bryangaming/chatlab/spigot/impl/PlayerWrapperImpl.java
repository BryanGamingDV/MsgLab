package me.bryangaming.chatlab.spigot.impl;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerWrapperImpl
        extends SenderWrapperImpl
        implements PlayerWrapper {

    private final Player player;

    public PlayerWrapperImpl(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getWorldName() {
        return player.getWorld().getName();
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public boolean isOp(){
        return player.isOp();
    }
}
