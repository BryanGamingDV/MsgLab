package me.bryangaming.chatlab.common.revisor.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public class BlockCmdRevisor implements Revisor {

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
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor-cmd." + revisorName + ".enabled");
    }

    public String revisor(PlayerWrapper player, String command) {

        Configuration formatFile = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        for (String commandName : formatFile.getStringList("revisor-cmd." + revisorName +  ".op.list")) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (formatFile.getBoolean("revisor-cmd." + revisorName + ".op.message.enabled")) {
                senderManager.sendMessage(player, formatFile.getString("revisor-cmd." + revisorName + ".op.message.format")
                        .replace("%command%", commandName));
            }
            return null;
        }


        for (String commandName : formatFile.getStringList("revisor-cmd." + revisorName + ".default.list")) {

            if (player.hasPermission("revisor-cmd." + revisorName + ".default.permission")) {
                break;
            }

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (formatFile.getBoolean("revisor-cmd." + revisorName + ".default.message.enabled")) {
                senderManager.sendMessage(player, formatFile.getString("revisor-cmd." + revisorName + ".default.message.format")
                        .replace("%command%", commandName));
            }

            return null;
        }


        return command;
    }

}
