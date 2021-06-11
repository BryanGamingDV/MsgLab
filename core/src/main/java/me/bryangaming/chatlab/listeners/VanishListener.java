package me.bryangaming.chatlab.listeners;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

public class VanishListener implements Listener {

    private final Configuration configFile;
    private final Jedis jedis;
    private PluginService pluginService;

    public VanishListener (PluginService pluginService){
        this.configFile = pluginService.getFiles().getConfigFile();
        this.jedis = pluginService.getRedisConnection().getJedisPool().getResource();
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onVanish(PlayerVanishStateChangeEvent event){

        if (!configFile.getBoolean("options.redis.enabled")){
            return;
        }

        if (jedis.hexists("onlinePlayer", event.getName())){
            jedis.hdel("onlinePlayer", event.getName());
        }else{
            jedis.hset("onlinePlayer", event.getName(), Bukkit.getServer().getName());
        }

    }

}
