package me.bryangaming.chatlab.revisor.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ConditionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConditionRevisor implements Revisor {


    private PluginService pluginService;
    private String revisorName;

    public ConditionRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor-cmd." + revisorName +  ".enabled");
    }

    @Override
    public String revisor(Player player, String command) {

        Configuration utils = pluginService.getFiles().getFormatsFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        ConditionManager conditionManager = pluginService.getPlayerManager().getConditionManager();

        String commandsPath = "revisor-cmd." + revisorName + ".commands";

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            return command;
        }

        for (String commandName : utils.getConfigurationSection(commandsPath).getKeys(false)) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (!conditionManager.hasTheCondition(player, utils.getString(commandsPath + "." + command))){
                if (!utils.getBoolean("revisor-cmd." + revisorName + ".message.enabled")){
                    senderManager.sendMessage(player, utils.getString("revisor-cmd." + revisorName + ".message.format"));
                }
                return null;
            }
        }

        return command;
    }
}
