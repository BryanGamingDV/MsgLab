package me.bryangaming.chatlab.common.wrapper;

import java.util.UUID;

public interface OfflinePlayerWrapper{

    UUID getUniqueId();

    String getName();

    boolean isOnline();

    PlayerWrapper getPlayer();

}
