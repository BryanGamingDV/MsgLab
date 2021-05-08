package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;

public class TabListener implements Listener {

    private PluginService pluginService;

    private Configuration formatsFile;

    public TabListener(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
    }


    @EventHandler
    public void onTab(PlayerCommandSendEvent playerCommandSendEvent) {
        if (!formatsFile.getBoolean("revisor-cmd.tab-module.filter.enabled")) {
            return;
        }

        Collection<String> commands = playerCommandSendEvent.getCommands();
        commands.clear();

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

        for (String completitions : formatsFile.getStringList("revisor-cmd.tab-module.filter.groups." + groupManager.getFitlerGroup(playerCommandSendEvent.getPlayer()))) {
            if (completitions.startsWith("@")) {
                commands.addAll(formatsFile.getStringList("revisor-cmd.tab-module.filter.groups." + completitions.substring(1)));
            }

            commands.add(completitions);
        }
    }
}
