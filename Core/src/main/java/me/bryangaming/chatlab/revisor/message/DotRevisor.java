package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DotRevisor implements Revisor {


    private PluginService pluginService;

    public DotRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor.dot-module.enabled");
    }

    @Override
    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

        int lettermin = utils.getInt("revisor.dot-module.min-word");

        if (string.length() <= lettermin) {
            return string;
        }

        string = string + ".";

        if (utils.getBoolean("revisor.dot-module.warning.enabled")) {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (playerMethod.hasPermission(onlinePlayer, "revisor.watch")) {
                    playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.dot-module.warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return string;
    }
}
