package me.bryangaming.chatlab.api.revisor;

import org.bukkit.entity.Player;

public interface Revisor {

    String getName();

    boolean isEnabled();

    String revisor(Player player, String message);
}
