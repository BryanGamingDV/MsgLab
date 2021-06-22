package me.bryangaming.chatlab.utils.hooks.login;

import me.bryangaming.chatlab.listeners.login.plugin.PlayerLockLoginEvent;
import ml.karmaconfigs.locklogin.api.modules.PluginModule;

public class LockLoginHook extends PluginModule {

    @Override
    public void onEnable() {
        getManager().registerListener(new PlayerLockLoginEvent());
    }

    @Override
    public void onDisable() {

    }
}
