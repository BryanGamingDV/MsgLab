package me.bryangaming.chatlab.revisor.message;

import com.google.common.base.Strings;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FloodRevisor implements Revisor {

    public static final String LETTERS = "messages." + "AaBbCcDdEeFfGgHhIiJjKkMmNnLlOoPpQqRrSsTtUuVvWwXxYyZz0123456789";

    private final PluginService pluginService;
    private final String revisorName;

    public FloodRevisor(PluginService pluginService, String revisorName) {
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

    @Override
    public String revisor(Player player, String string) {

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        int floodstatus = 0;
        int minflood = Math.max(0, filtersFile.getInt("message." + revisorName + ".min-chars"));

        for (char letter : LETTERS.toCharArray()) {

            for (int count = 50; count > minflood; count--) {

                String letterchanged = String.valueOf(letter);

                if (string.contains(Strings.repeat(letterchanged, count))) {

                    if (floodstatus < 1) {
                        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();
                        List<String> listActions = filtersFile.getStringList("message." + revisorName + ".actions");

                        if (!listActions.isEmpty()) {
                            actionManager.execute(player, listActions);
                        }

                        if (filtersFile.getBoolean("message." + revisorName + ".cancel-message")){
                            return null;
                        }
                    }

                    string = string
                            .replace(Strings.repeat(letterchanged, count), letterchanged);

                    floodstatus++;
                }
            }
        }

        return string;

    }

}
