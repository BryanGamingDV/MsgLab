package me.bryangaming.chatlab.bungeecord.impl;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.YamlConfiguration;

import java.util.UUID;

public class ProxiedPlayerImpl extends ProxiedSenderImpl implements PlayerWrapper {

    private final ProxiedPlayer proxiedPlayer;

    public ProxiedPlayerImpl(ProxiedPlayer player){
        super(player);
        this.proxiedPlayer = player;

    }

    @Override
    public UUID getUniqueId() {
        return proxiedPlayer.getUniqueId();
    }

    @Override
    public String getWorldName() {
        proxiedPlayer.getServer().getInfo().getName();
    }
}
