package me.bryangaming.chatlab.listeners;

import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

public class CMIVanishListener implements Listener {

    private PluginService pluginService;

    private final Configuration configFile;
    private Jedis jedis;

    public CMIVanishListener(PluginService pluginService) {
        this.pluginService = pluginService;

        this.configFile = pluginService.getFiles().getConfigFile();

        if (pluginService.getRedisConnection() != null) {
            this.jedis = pluginService.getRedisConnection().getJedisPool().getResource();
        }
    }

    @EventHandler
    public void onVanish(CMIPlayerVanishEvent event){
        if (!configFile.getBoolean("options.redis.enabled")){
            return;
        }
        jedis.hdel("onlinePlayer", event.getPlayer().getName());
    }

    @EventHandler
    public void onUnVanish(CMIPlayerUnVanishEvent event){
        if (!configFile.getBoolean("options.redis.enabled")){
            return;
        }
        jedis.hset("onlinePlayer", event.getPlayer().getName(), Bukkit.getServer().getName());
    }
}
