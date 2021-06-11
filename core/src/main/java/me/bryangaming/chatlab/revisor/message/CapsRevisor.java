package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CapsRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public CapsRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }
    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFiltersFile().getBoolean("message." + revisorName + ".enabled");
    }

    public String revisor(Player player, String string) {

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        int mayusmin = filtersFile.getInt("message." + revisorName + ".min-mayus", -1);
        int mayuscount = 0;

        if (mayusmin < 0) {
            senderManager.sendMessage(player, "%p &fEmmm, you either didn't put anything in the config, or you are trying to detect invisible mayus?");
            senderManager.sendMessage(player, "EasterEgg #4");
        }

        for (char letter : string.toCharArray()) {

            if (Character.isUpperCase(letter)) {
                mayuscount++;
            }
        }

        if (mayuscount <= mayusmin) {
            return string;
        }

        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();
        List<String> listActions = filtersFile.getStringList("message." + revisorName + ".actions");

        if (!listActions.isEmpty()) {
            actionManager.execute(player, listActions);
        }

        return StringUtils.capitalize(string);
    }

}
