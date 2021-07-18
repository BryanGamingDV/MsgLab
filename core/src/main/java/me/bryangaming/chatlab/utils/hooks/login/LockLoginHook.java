package me.bryangaming.chatlab.utils.hooks.login;

import eu.locklogin.api.module.PluginModule;
import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.listeners.login.plugin.PlayerLockLoginEvent;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class LockLoginHook extends PluginModule {

    @Override
    public void enable() {
        System.out.println("test");
        try {
            Class<?> chatLabClass = Class.forName("me.bryangaming.chatlab.ChatLab");
            Field field = chatLabClass.getField("chatLab");

            PluginService service = (PluginService) field.get(null);
            service.getPlugin().getLogger().info("test");
            getConsole().sendMessage("&aEnabled ChatLab support for LockLogin");
            getPlugin().registerListener(new PlayerLockLoginEvent(service));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void disable() {
        stopTasks();

        getConsole().sendMessage("&cDisabling ChatLab support for LockLogin");
    }
}
