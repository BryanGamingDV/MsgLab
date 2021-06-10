package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

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
        return pluginService.getFiles().getFiltersFile().getBoolean("message."+ revisorName +".enabled");
    }

    @Override
    public String revisor(Player player, String string) {

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
        int lettermin = filtersFile.getInt("message." + revisorName + ".min-word");

        if (string.length() <= lettermin) {
            return string;
        }

        string = string + ".";

        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();
        List<String> listActions = filtersFile.getStringList("message." + revisorName + ".actions");

        if (!listActions.isEmpty()) {
            actionManager.execute(player, listActions);
        }

        return string;
    }
}
