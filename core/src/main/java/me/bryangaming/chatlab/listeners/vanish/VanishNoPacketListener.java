package me.bryangaming.chatlab.listeners.vanish;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import redis.clients.jedis.Jedis;

public class VanishNoPacketListener implements Listener{

    private final Configuration configFile;
    private Jedis jedis;

    public VanishNoPacketListener(PluginService pluginService) {
        this.configFile = pluginService.getFiles().getConfigFile();

        if (pluginService.getRedisConnection() != null) {
            this.jedis = pluginService.getRedisConnection().getJedisPool().getResource();
        }
    }

    @EventHandler
    public void onVanish(VanishStatusChangeEvent event){

        if (!configFile.getBoolean("options.redis.enabled")){
            return;
        }

        try {
            if (VanishNoPacket.getManager().isVanished(event.getPlayer())){
                jedis.hdel("onlinePlayer", event.getPlayer().getName());
            }else{
                jedis.hset("onlinePlayer", event.getPlayer().getName(), Bukkit.getServer().getName());
            }
        }catch (VanishNotLoadedException error){
            return;
        }
    }
}
