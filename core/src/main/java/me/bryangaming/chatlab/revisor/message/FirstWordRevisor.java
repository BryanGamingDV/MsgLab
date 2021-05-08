package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FirstWordRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public FirstWordRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor." + revisorName + ".enabled");
    }

    @Override
    public String revisor(Player player, String message) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        String firstLetter = String.valueOf(message.charAt(0));

        if (firstLetter.equals(firstLetter.toUpperCase())){
            return message;
        }

        message = message.replaceFirst(firstLetter, firstLetter.toUpperCase());

        if (utils.getBoolean("revisor." + revisorName + ".warning.enabled")) {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (senderManager.hasPermission(onlinePlayer, "revisor.watch")) {
                    senderManager.sendMessage(onlinePlayer, utils.getString("revisor." + revisorName + ".warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return message;
    }
}
