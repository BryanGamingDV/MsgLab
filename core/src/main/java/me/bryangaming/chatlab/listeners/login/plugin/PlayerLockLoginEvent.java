package me.bryangaming.chatlab.listeners.login.plugin;


import me.bryangaming.chatlab.events.PlayerLoginEvent;
import ml.karmaconfigs.locklogin.api.modules.api.event.ModuleEventHandler;
import ml.karmaconfigs.locklogin.api.modules.api.event.user.UserAuthenticateEvent;
import ml.karmaconfigs.locklogin.api.modules.api.event.util.EventListener;
import org.bukkit.Bukkit;

public class PlayerLockLoginEvent implements EventListener {


    @ModuleEventHandler
    public void onLogin(UserAuthenticateEvent event){
        Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(Bukkit.getPlayer(event.getPlayer().getUUID())));
    }
}
