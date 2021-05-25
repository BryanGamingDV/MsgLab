package me.bryangaming.chatlab.common.modules;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.configuration.ConfigurationSectionWrapper;


import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecoverDataModule implements Module {

    private final PluginService pluginService;

    public RecoverDataModule(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public void start() {
        Configuration players = pluginService.getFiles().getPlayersFile();
        Map<UUID, UserData> hashMap = pluginService.getCache().getUserDatas();


        ConfigurationSectionWrapper playerList = players.getConfigurationSection("players.");

        if (playerList == null){
            return;
        }

        for (String keys : playerList.getKeys(false)){
            UUID uuid = UUID.fromString(keys);
            List<String> ignoredlist = players.getStringList("players." + keys + ".players-ignored");

            if (ignoredlist.isEmpty()) {
                return;
            }

            hashMap.put(uuid, new UserData(uuid));

            Map<UUID, List<String>> playerIgnored = pluginService.getCache().getIgnorelist();
            playerIgnored.put(uuid, ignoredlist);
        }
    }
}
