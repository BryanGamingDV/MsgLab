package me.bryangaming.chatlab.revisor.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BlockRevisor implements Revisor{

    private PluginService pluginService;

    public BlockRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor-cmd.commands-module.block.enabled");
    }

    public String revisor(Player player, String command) {

        Configuration formatFile = pluginService.getFiles().getFormatsFile();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

        for (String commandName : formatFile.getStringList("revisor-cmd.commands-module.block.default.list")) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (formatFile.getBoolean("revisor-cmd.commands-module.block.op.message.enabled")) {
                playerMethod.sendMessage(player, formatFile.getString("revisor-cmd.commands-module.block.op.message.format")
                        .replace("%command%", commandName));
            }
            return null;
        }


        for (String commandName : formatFile.getStringList("revisor-cmd.commands-module.block.default.list")) {

            if (player.hasPermission("revisor-cmd.commands-module.block.default.permission")) {
                break;
            }

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (formatFile.getBoolean("revisor-cmd.commands-module.block.default.message.format")) {
                playerMethod.sendMessage(player, formatFile.getString("revisor-cmd.commands-module.block.default.message.format")
                        .replace("%command%", commandName));
            }

            return null;
        }


        return command;
    }

}
