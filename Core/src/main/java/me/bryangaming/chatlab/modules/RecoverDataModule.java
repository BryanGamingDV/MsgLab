package me.bryangaming.chatlab.modules;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecoverDataModule implements Module{

    private final PluginService pluginService;

    public RecoverDataModule(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public void start() {
        Configuration players = pluginService.getFiles().getPlayers();
        Map<UUID, UserData> hashMap = pluginService.getCache().getUserDatas();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            UUID playeruuid = player.getUniqueId();
            List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

            if (ignoredlist.isEmpty()) {
                return;
            }

            hashMap.put(playeruuid, new UserData(playeruuid));


            Map<UUID, List<String>> playerIgnored = pluginService.getCache().getIgnorelist();
            playerIgnored.put(playeruuid, ignoredlist);
        }
    }
}
