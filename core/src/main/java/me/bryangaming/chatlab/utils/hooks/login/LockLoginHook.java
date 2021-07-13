package me.bryangaming.chatlab.utils.hooks.login;

import eu.locklogin.api.module.PluginModule;
import me.bryangaming.chatlab.listeners.login.plugin.PlayerLockLoginEvent;

public class LockLoginHook extends PluginModule {

    @Override
    public void enable() {
        getConsole().sendMessage("&aEnabled ChatLab support for LockLogin");
        getPlugin().registerListener(new PlayerLockLoginEvent());
    }

    @Override
    public void disable() {
        stopTasks();

        getConsole().sendMessage("&cDisabling ChatLab support for LockLogin");
    }
}
