package me.bryangaming.chatlab.common.listeners.command;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.CommandSpyEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.Configuration;

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

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration command = pluginService.getFiles().getCommandFile();

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

            senderManager.sendMessage(player, commandSpyFormat);
            senderManager.playSound(player, SoundEnum.RECEIVE_COMMANDSPY);
        });
    }
}
