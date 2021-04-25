package me.bryangaming.chatlab.revisor.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BlockRevisor implements Revisor{

    private PluginService pluginService;

    public BlockRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String revisor(Player player, String command) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("revisor-cmd.commands-module.block.enabled")) {
            return command;
        }

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        for (String commandName : utils.getStringList("revisor-cmd.commands-module.block.default.list")) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (utils.getBoolean("revisor-cmd.commands-module.block.op.message.enabled")) {
                playerMethod.sendMessage(player, utils.getString("revisor-cmd.commands-module.block.op.message.format"));
            }

            return null;
        }


        for (String commandName : utils.getStringList("revisor-cmd.commands-module.block.default.list")) {

            if (player.hasPermission("revisor-cmd.commands-module.block.default.permission")) {
                break;
            }

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (utils.getBoolean("revisor-cmd.commands-module.block.default.message.format")) {
                playerMethod.sendMessage(player, utils.getString("revisor-cmd.commands-module.block.default.message.format"));
            }

            return null;
        }


        return command;
    }

}
