package me.bryangaming.chatlab.api;


import java.util.UUID;

public interface Option {

    void enableOption(UUID uuid);

    void disableOption(UUID uuid);

}
