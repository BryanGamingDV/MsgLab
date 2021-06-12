package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.SendDataEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

public class SendDataListener implements Listener {

    private final PluginService pluginService;

    public SendDataListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onSend(SendDataEvent event){

        Configuration configFile = pluginService.getFiles().getConfigFile();

        if (configFile.getBoolean("options.bungeecord")) {
            try (Jedis jedis = pluginService.getRedisConnection().getJedisPool().getResource()) {
                jedis.hset("onlinePlayer", event.getPlayer().getName(), Bukkit.getServer().getName());
            }
        }

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        if (senderManager.hasPermission(event.getPlayer(), "helpop", "watch")) {
            event.getUserData().toggleHelpOp(true);
        }

        event.getUserData().setChannelGroup(configFile.getString("modules.channel.default-channel"));
    }
}
