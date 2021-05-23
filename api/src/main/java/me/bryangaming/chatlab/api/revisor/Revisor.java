package me.bryangaming.chatlab.api.revisor;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public interface Revisor {

    String getName();

    boolean isEnabled();

    String revisor(PlayerWrapper player, String message);
}
