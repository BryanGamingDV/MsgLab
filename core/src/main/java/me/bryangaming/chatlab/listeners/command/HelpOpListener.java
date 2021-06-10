package me.bryangaming.chatlab.listeners.command;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.HelpOpEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.redis.MessageType;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HelpOpListener implements Listener {


    private PluginService pluginService;

    public HelpOpListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onHelpOp(HelpOpEvent helpopEvent) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration configFile = pluginService.getFiles().getConfigFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        if (configFile.getBoolean("options.bungeecord")){
            pluginService.getRedisConnection().sendMessage("chatlab", MessageType.HELPOP, messagesFile.getString("helpop.format")
                    .replace("%player%", helpopEvent.getPlayer().getName())
                    .replace("%message%", helpopEvent.getMessage()));
            return;
        }

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (!senderManager.hasPermission(onlinePlayer, "helpop", "watch") || !onlineCache.isPlayerHelpOp()) {
                return;
            }

            senderManager.sendMessage(onlinePlayer, messagesFile.getString("helpop.format")
                    .replace("%player%", helpopEvent.getPlayer().getName())
                    .replace("%message%", helpopEvent.getMessage()));
            senderManager.playSound(onlinePlayer, SoundEnum.RECEIVE_HELPOP);
        });
    }
}
