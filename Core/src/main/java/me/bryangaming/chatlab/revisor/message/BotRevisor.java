package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BotRevisor implements Revisor {

    private PluginService pluginService;

    public BotRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String revisor(Player player, String message) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        if (!utils.getBoolean("filters.bot-response.enabled")){
            return message;
        }
        for (String keys : utils.getConfigurationSection("fitlers.bot-response.lists").getKeys(false)) {
            if (!message.startsWith(utils.getString("fitlers.bot-response.lists." + keys + ".question"))) {
                continue;
            }

            playerMethod.sendMessage(player, utils.getString("fitlers.bot-response.lists." + keys + ".question"));

            if (!(utils.getStringList("fitlers.bot-response.lists." + keys + ".commands").isEmpty())) {
                utils.getStringList("fitlers.bot-response.lists." + keys + ".commands")
                        .forEach(command -> playerMethod.sendCommand(player, command));
            }
        }

        return message;
    }
}
