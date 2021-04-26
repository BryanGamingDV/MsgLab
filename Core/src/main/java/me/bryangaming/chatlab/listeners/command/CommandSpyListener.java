package me.bryangaming.chatlab.listeners.command;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.CommandSpyEvent;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class CommandSpyListener implements Listener {

    private PluginService pluginService;

    public CommandSpyListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onCommandSpy(CommandSpyEvent commandSpyEvent) {

        SenderManager playersender = pluginService.getPlayerManager().getSender();
        Configuration command = pluginService.getFiles().getCommand();

        List<String> blockedCommands = command.getStringList("commands.commandspy.blocked-commands");

        for (String blockedCommand : blockedCommands) {
            if (blockedCommand.startsWith(commandSpyEvent.getMessage())) {
                return;
            }
        }

        String commandSpyFormat = command.getString("commands.commandspy.format")
                .replace("%sender%", commandSpyEvent.getSender())
                .replace("%command%", "/" + commandSpyEvent.getMessage());

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            UserData watcherSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

            if (!watcherSpy.isCommandspyMode()) {
                return;
            }

            playersender.sendMessage(player, commandSpyFormat);
            playersender.sendSound(player, SoundEnum.RECEIVE_COMMANDSPY);
        });
    }
}
