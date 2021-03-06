package me.bryangaming.chatlab.revisor.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockCmdRevisor implements Revisor{

    private final PluginService pluginService;
    private final Configuration filtersFile;
    private final String revisorName;

    public BlockCmdRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.filtersFile = pluginService.getFiles().getFiltersFile();
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFiltersFile().getBoolean("commands." + revisorName + ".enabled");
    }

    public String revisor(Player player, String command) {

        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();

        String playerGroup = pluginService.getPlayerManager().getGroupManager().getGroup(player, filtersFile.getConfigurationSection("commands.commands-module.block"));

        if (!playerGroup.equalsIgnoreCase("default")) {
            for (String commandName : filtersFile.getStringList("commands." + revisorName + ".groups. " + playerGroup + ".list")) {
                if (!command.equalsIgnoreCase(commandName)) {
                    continue;
                }

                List<String> actions = filtersFile.getStringList("commands." + revisorName + ".op.actions");

                if (!actions.isEmpty()) {

                    actions.replaceAll(action -> action.replace("%command%", command));
                    actionManager.execute(player, actions);
                }

                return null;
            }

        }

        for (String commandName : filtersFile.getStringList("commands." + revisorName +  ".op.list")) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            List<String> actions = filtersFile.getStringList("commands." + revisorName + ".op.actions");

            if (!actions.isEmpty()) {

                actions.replaceAll(action -> action.replace("%command%", command));
                actionManager.execute(player, actions);
            }

            return null;
        }




        for (String commandName : filtersFile.getStringList("commands." + revisorName + ".default.list")) {

            if (player.hasPermission("commands." + revisorName + ".default.permission")) {
                break;
            }

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            List<String> actions = filtersFile.getStringList("commands." + revisorName + ".default.actions");

            if (!actions.isEmpty()) {

                actions.replaceAll(action -> action.replace("%command%", command));
                actionManager.execute(player, actions);
            }

            return null;
        }


        return command;
    }

}
