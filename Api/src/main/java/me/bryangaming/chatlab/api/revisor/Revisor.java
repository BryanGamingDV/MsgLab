package me.bryangaming.chatlab.api.revisor;

import org.bukkit.entity.Player;

public interface Revisor {

    boolean isEnabled();

    String revisor(Player player, String message);
}
