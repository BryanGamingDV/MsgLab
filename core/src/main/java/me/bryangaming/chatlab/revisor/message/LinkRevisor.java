package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class LinkRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public LinkRevisor(PluginService pluginService, String revisorName) {
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

        if (filtersFile.getBoolean("message." + revisorName + ".block-point")) {
            if (!string.contains(".")) {
                return string;
            }

            for (String word : string.split(" ")) {
                if (word.contains(".")) {
                    string = string.replace(word, filtersFile.getString("message." + revisorName + ".replace-link"));
                    break;
                }
            }

            sendMessage(player, " . ");
            if (filtersFile.getBoolean("message." + revisorName + ".cancel-message")){
                return null;
            }
            string = string.replace(".", filtersFile.getString("message." + revisorName + ".replace-link"));

            if (!(player.isOnline())) {
                return null;
            }

            if (string.trim().isEmpty()) {
                return null;
            }

            return string;
        }


        List<String> blockList = filtersFile.getStringList("message." + revisorName + ".blocked-links");

        for (String blockedWord : blockList) {
            for (String word : string.split(" ")) {
                if (!string.contains(blockedWord)) {
                    continue;
                }

                string = string.replace(word, filtersFile.getString("message." + revisorName + ".replace-link"));
                sendMessage(player, blockedWord);

                if (!(player.isOnline())) {
                    return null;
                }

                if (string.trim().isEmpty()) {
                    return null;
                }

            }

        }

        return string;

    }

    private void sendMessage(Player player, String blockedword) {
        Configuration filtersFile = pluginService.getFiles().getFiltersFile();

        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();
        List<String> listActions = filtersFile.getStringList("message." + revisorName + ".actions");

        if (!listActions.isEmpty()) {
            listActions.replaceAll(text -> text.replace("%words%", blockedword));
            actionManager.execute(player, listActions);
        }
    }

}
