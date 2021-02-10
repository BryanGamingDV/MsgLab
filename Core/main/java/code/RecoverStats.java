package code;

import code.data.UserData;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecoverStats {

    private final PluginService pluginService;

    public RecoverStats(PluginService pluginService){
        this.pluginService = pluginService;
        setup();
    }


    private void setup(){
        Configuration players = pluginService.getFiles().getPlayers();
        Map<UUID, UserData> hashMap = pluginService.getCache().getPlayerUUID();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            UUID playeruuid = player.getUniqueId();
            List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

            if (ignoredlist.isEmpty()){
                return;
            }

            hashMap.put(playeruuid, new UserData(playeruuid));


            Map<UUID, List<String>> playerIgnored = pluginService.getCache().getIgnorelist();
            playerIgnored.put(playeruuid, ignoredlist);
        }
    }
}
