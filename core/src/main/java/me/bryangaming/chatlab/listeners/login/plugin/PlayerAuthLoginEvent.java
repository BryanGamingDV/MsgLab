package me.bryangaming.chatlab.listeners.login.plugin;

import fr.xephi.authme.events.LoginEvent;
import me.bryangaming.chatlab.events.PlayerLoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerAuthLoginEvent implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event){
        Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(event.getPlayer()));
    }
}
