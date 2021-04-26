package me.bryangaming.chatlab.revisor.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ConditionManager;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConditionRevisor implements Revisor {


    private PluginService pluginService;

    public ConditionRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String revisor(Player player, String command) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean(" revisor-cmd.commands-module.conditions.enabled")) {
            return command;
        }

        SenderManager playerMethod = pluginService.getPlayerManager().getSender();
        ConditionManager conditionManager = pluginService.getPlayerManager().getConditionManager();

        String commandsPath = "revisor-cmd.commands-module.conditions.commands";

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            return command;
        }

        for (String commandName : utils.getConfigurationSection(commandsPath).getKeys(false)) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (!conditionManager.hasTheCondition(player, utils.getString(commandsPath + "." + command))){
                if (!utils.getBoolean("revisor-cmd.commands.conditions.message.enabled")){
                    playerMethod.sendMessage(player, utils.getString("revisor-cmd.commands.conditions.message.format"));
                }
                return null;
            }
        }

        return command;
    }
}
