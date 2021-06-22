package me.bryangaming.chatlab.listeners.login.plugin;


import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.PlayerLoginEvent;
import me.bryangaming.chatlab.utils.BukkitUtils;
import ml.karmaconfigs.locklogin.api.modules.api.event.ModuleEventHandler;
import ml.karmaconfigs.locklogin.api.modules.api.event.user.UserAuthenticateEvent;
import ml.karmaconfigs.locklogin.api.modules.api.event.util.EventListener;
import org.apache.commons.collections.BagUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class PlayerLockLoginEvent implements EventListener {


    @ModuleEventHandler
    public void onLogin(UserAuthenticateEvent event){
        Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(Bukkit.getPlayer(event.getPlayer().getUUID())));
    }
}
