package me.bryangaming.chatlab.revisor.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BlockCmdRevisor implements Revisor{

    private final PluginService pluginService;
    private final String revisorName;

    public BlockCmdRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
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

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        for (String commandName : filtersFile.getStringList("commands." + revisorName +  ".op.list")) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (filtersFile.getBoolean("commands." + revisorName + ".op.message.enabled")) {
                senderManager.sendMessage(player, filtersFile.getString("commands." + revisorName + ".op.message.format")
                        .replace("%command%", commandName));
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

            if (filtersFile.getBoolean("commands." + revisorName + ".default.message.enabled")) {
                senderManager.sendMessage(player, filtersFile.getString("commands." + revisorName + ".default.message.format")
                        .replace("%command%", commandName));
            }

            return null;
        }


        return command;
    }

}
