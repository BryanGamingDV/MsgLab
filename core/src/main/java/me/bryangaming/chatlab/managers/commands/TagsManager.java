package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.Map;

public class TagsManager {

    private final PluginService pluginService;
    private final Configuration filtersFile;


    public TagsManager(PluginService pluginService){
        this.pluginService = pluginService;
        this.filtersFile = pluginService.getFiles().getFiltersFile();
    }

    public String replaceTagsVariables(Player player, String message) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        Map<String, String> hashmapTag = userData.gethashTags();

        for (String group : filtersFile.getConfigurationSection("tags").getKeys(false)) {

            if (group.equalsIgnoreCase("config")){
                continue;
            }

            String variableTag = filtersFile.getString("tags." + group + ".variable");

            if (variableTag == null) {
                System.out.println("Nulltag " + group);
                continue;
            }


            if (!message.contains(variableTag)) {
                continue;
            }

            if (!player.hasPermission(variableTag)) {
                message = message.replace(variableTag, "");
                continue;
            }


            if (!hashmapTag.containsKey(group)) {
                message = message.replace(variableTag, "");
                continue;
            }

            message = message.replace(variableTag, hashmapTag.get(group));

        }

        return message;

    }
}
