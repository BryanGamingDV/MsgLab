package code.managers;

import code.PluginService;
import code.utils.Configuration;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;

public class FitlerMethod {

    private PluginService pluginService;

    public FitlerMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void onTab(PlayerCommandSendEvent playerCommandSendEvent) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("fitler-cmd.enabled")) {
            return;
        }


        Collection<String> commands = playerCommandSendEvent.getCommands();
        commands.clear();

        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();

        for (String completitions : utils.getStringList("fitler-cmd.groups." + groupMethod.getFitlerGroup(playerCommandSendEvent.getPlayer()))) {
            if (completitions.startsWith("@")) {
                commands.addAll(utils.getStringList("fitler.cmd.groups." + completitions.substring(1)));
            }

            commands.add(completitions);
        }
    }

}
