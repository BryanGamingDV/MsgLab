package me.bryangaming.chatlab.common.revisor.message;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public class DotRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public DotRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor."+ revisorName +".enabled");
    }

    @Override
    public String revisor(PlayerWrapper player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        int lettermin = utils.getInt("revisor." + revisorName + ".min-word");

        if (string.length() <= lettermin) {
            return string;
        }

        string = string + ".";

        if (utils.getBoolean("revisor." + revisorName + ".warning.enabled")) {
            ServerWrapper.getData().getOnlinePlayers().forEach(onlinePlayer -> {
                if (senderManager.hasPermission(onlinePlayer, "revisor.watch")) {
                    senderManager.sendMessage(onlinePlayer, utils.getString("revisor." + revisorName + ".warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return string;
    }
}
