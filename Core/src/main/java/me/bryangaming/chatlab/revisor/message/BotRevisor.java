package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BotRevisor implements Revisor {

    private PluginService pluginService;

    public BotRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("filters.enabled");
    }

    public String revisor(Player player, String message) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

        if (!utils.getBoolean("filters.bot-response.enabled")){
            return message;
        }
        for (String keys : utils.getConfigurationSection("filters.bot-response.lists").getKeys(false)) {
            if (!message.startsWith(utils.getString("filters.bot-response.lists." + keys + ".question"))) {
                continue;
            }

            playerMethod.sendMessage(player, utils.getString("filters.bot-response.lists." + keys + ".question"));

            if (!(utils.getStringList("filters.bot-response.lists." + keys + ".commands").isEmpty())) {
                utils.getStringList("filters.bot-response.lists." + keys + ".commands")
                        .forEach(command -> playerMethod.sendCommand(player, command));
            }
        }

        return message;
    }
}
