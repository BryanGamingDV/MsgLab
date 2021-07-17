package me.bryangaming.chatlab.listeners.login.plugin;

import com.nickuc.login.api.events.AuthenticateEvent;
import me.bryangaming.chatlab.events.PlayerLoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerNLoginEvent implements Listener{

    @EventHandler
    public void onLogin(AuthenticateEvent event) {
        Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(event.getPlayer()));
    }
}
