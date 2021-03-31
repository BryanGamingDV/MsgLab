package code.revisor.commands;

import code.PluginService;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.entity.Player;

public class BlockRevisor {

    private PluginService pluginService;

    public BlockRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String revisor(Player player, String command) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("revisor-cmd.commands-module.enabled")) {
            return command;
        }

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        for (String commandName : utils.getStringList("revisor-cmd.commands-module.commands.default.list")) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (utils.getBoolean("revisor-cmd.commands-module.commands.op.message.enabled")) {
                playerMethod.sendMessage(player, utils.getString("revisor-cmd.commands-module.commands.op.message.format"));
            }

            return null;
        }


        for (String commandName : utils.getStringList("revisor-cmd.commands-module.commands.default.list")) {

            if (player.hasPermission("revisor-cmd.commands-module.commands.default.permission")) {
                break;
            }

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (utils.getBoolean("revisor-cmd.commands-module.commands.default.message.format")) {
                playerMethod.sendMessage(player, utils.getString("revisor-cmd.commands-module.commands.default.message.format"));
            }

            return null;
        }


        return command;
    }

}
