package me.bryangaming.chatlab.common.wrapper;

import java.util.UUID;

public interface PlayerWrapper extends SenderWrapper{

    UUID getUniqueId();

    String getWorldName();

}
