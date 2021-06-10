package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;

public class TabListener implements Listener {

    private final PluginService pluginService;
    private final Configuration filtersFile;

    public TabListener(PluginService pluginService) {
        this.pluginService = pluginService;
        this.filtersFile = pluginService.getFiles().getFiltersFile();
    }


    @EventHandler
    public void onTab(PlayerCommandSendEvent playerCommandSendEvent) {

        if (!filtersFile.getBoolean("commands.tab-module.filter.enabled")) {
            return;
        }

        Collection<String> commands = playerCommandSendEvent.getCommands();
        commands.clear();
        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

        for (String completitions : filtersFile.getStringList("revisor-cmd.tab-module.filter.groups." + groupManager.getFitlerGroup(playerCommandSendEvent.getPlayer()))) {
            if (completitions.startsWith("@")) {
                commands.addAll(filtersFile.getStringList("revisor-cmd.tab-module.filter.groups." + completitions.substring(1)));
            }

            commands.add(completitions);
        }
    }
}
