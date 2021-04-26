package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FirstWordRevisor implements Revisor {

    private PluginService pluginService;

    public FirstWordRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public String revisor(Player player, String message) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

        if (!utils.getBoolean("revisor.first-mayus-module.enabled")){
            return message;
        }

        String firstLetter = String.valueOf(message.charAt(0));

        if (firstLetter.equals(firstLetter.toUpperCase())){
            return message;
        }

        message = message.replaceFirst(firstLetter, firstLetter.toUpperCase());


        if (utils.getBoolean("revisor.first-mayus-module.warning.enabled")) {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (playerMethod.hasPermission(onlinePlayer, "revisor.watch")) {
                    playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.first-mayus-module.warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return message;
    }
}
