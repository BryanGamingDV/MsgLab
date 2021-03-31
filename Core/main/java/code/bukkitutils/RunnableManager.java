package code.bukkitutils;

import code.PluginService;
import code.managers.player.PlayerMessage;
import code.managers.player.PlayerStatic;
import code.utils.string.VariableUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunnableManager {

    private final PluginService pluginService;

    public RunnableManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void waitSecond(Player player, int second, String path) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {
            PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
            playersender.sendMessage(player, path);

        }, 20L * second);
    }

    public void waitTicks(Player player, long ticks, String path) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {
            PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
            playersender.sendMessage(player, path);
        }, ticks);
    }

    public void waitSecond(Player player, int second, String... paths) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {

            PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
            for (String path : paths) {
                playersender.sendMessage(player, path);

            }
        }, 20L * second);
    }

    public void waitTicks(Player player, long ticks, String... paths) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {

            PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
            for (String path : paths) {
                playersender.sendMessage(player, path);

            }
        }, ticks);
    }


    public void sendCommand(CommandSender sender, String path) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), () -> {

            Bukkit.dispatchCommand(sender, path);

        }, 20L);
    }


}
