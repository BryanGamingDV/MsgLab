package me.bryangaming.chatlab.listeners.vanish;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

public class VanishListener implements Listener {

    private final Configuration configFile;
    private Jedis jedis;

    public VanishListener (PluginService pluginService){
        this.configFile = pluginService.getFiles().getConfigFile();

        if (configFile.getBoolean("options.redis.enabled")) {
            this.jedis = pluginService.getRedisConnection().getJedisPool().getResource();
        }
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
