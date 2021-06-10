package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BotRevisor implements Revisor {

    private final String revisorName;
    private final PluginService pluginService;

    public BotRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFiltersFile().getBoolean("messages." + revisorName + ".enabled");
    }

    public String revisor(Player player, String message) {

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();


        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();

        if (!filtersFile.getBoolean("message." + revisorName + ".enabled")){
            return message;
        }
        for (String keys : filtersFile.getConfigurationSection("message." + revisorName + ".lists").getKeys(false)) {
            if (!message.startsWith(filtersFile.getString("message." + revisorName + ".lists." + keys + ".question"))) {
                continue;
            }

            senderManager.sendMessage(player, filtersFile.getString("message." + revisorName + ".lists." + keys + ".question"));

            if (!(filtersFile.getStringList("message." + revisorName + ".lists." + keys + ".actions").isEmpty())) {
                actionManager.execute(player, filtersFile.getStringList("message." + revisorName + ".lists." + keys + ".actions"));
            }
        }

        return message;
    }
}
