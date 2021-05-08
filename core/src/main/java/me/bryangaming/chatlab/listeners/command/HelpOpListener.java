package me.bryangaming.chatlab.listeners.command;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.HelpOpEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
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
    public void onCommandSpy(HelpOpEvent helpopEvent) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration command = pluginService.getFiles().getCommandFile();

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (!senderManager.hasPermission(onlinePlayer, "commands.helpop.watch") || !onlineCache.isPlayerHelpOp()) {
                return;
            }

            senderManager.sendMessage(onlinePlayer, command.getString("commands.helpop.message")
                    .replace("%player%", onlinePlayer.getName())
                    .replace("%message%", helpopEvent.getMessage()));
            senderManager.playSound(onlinePlayer, SoundEnum.RECEIVE_HELPOP);
        });
    }
}
