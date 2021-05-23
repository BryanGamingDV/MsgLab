package me.bryangaming.chatlab.common.managers;

import me.bryangaming.chatlab.common.PluginService;

import org.bukkit.command.CommandSender;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public class RunnableManager {

    private final PluginService pluginService;

    public RunnableManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void waitSecond(PlayerWrapper player, int second, String path) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {
            SenderManager senderManager = pluginService.getPlayerManager().getSender();
            senderManager.sendMessage(player, path);

        }, 20L * second);
    }

    public void waitTicks(PlayerWrapper player, long ticks, String path) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {
            SenderManager senderManager = pluginService.getPlayerManager().getSender();
            senderManager.sendMessage(player, path);
        }, ticks);
    }

    public void waitSecond(PlayerWrapper player, int second, String... paths) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {

            SenderManager senderManager = pluginService.getPlayerManager().getSender();
            for (String path : paths) {
                senderManager.sendMessage(player, path);

            }
        }, 20L * second);
    }

    public void waitTicks(PlayerWrapper player, long ticks, String... paths) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {

            SenderManager senderManager = pluginService.getPlayerManager().getSender();
            for (String path : paths) {
                senderManager.sendMessage(player, path);

            }
        }, ticks);
    }


    public void sendCommand(CommandSender sender, String path) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {

            Bukkit.dispatchCommand(sender, path);

        }, 20L);
    }


}
