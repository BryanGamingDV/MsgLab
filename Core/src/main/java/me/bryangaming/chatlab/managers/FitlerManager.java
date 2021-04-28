package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;

public class FitlerManager {

    private PluginService pluginService;

    public FitlerManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void onTab(PlayerCommandSendEvent playerCommandSendEvent) {

        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (!utils.getBoolean("revisor-cmd.tab-module.filter.enabled")) {
            return;
        }


        Collection<String> commands = playerCommandSendEvent.getCommands();
        commands.clear();

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

        for (String completitions : utils.getStringList("revisor-cmd.tab-module.filter.groups." + groupManager.getFitlerGroup(playerCommandSendEvent.getPlayer()))) {
            if (completitions.startsWith("@")) {
                commands.addAll(utils.getStringList("revisor-cmd.tab-module.filter.groups." + completitions.substring(1)));
            }

            commands.add(completitions);
        }
    }

}
