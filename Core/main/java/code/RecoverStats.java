package code;

import code.cache.UserData;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecoverStats {

    private final Manager manager;

    public RecoverStats(Manager manager){
        this.manager = manager;
        setup();
    }


    private void setup(){
        Configuration players = manager.getFiles().getPlayers();
        Map<UUID, UserData> hashMap = manager.getCache().getPlayerUUID();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            UUID playeruuid = player.getUniqueId();
            List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

            if (ignoredlist.isEmpty()){
                return;
            }

            hashMap.put(playeruuid, new UserData(playeruuid));


            Map<UUID, List<String>> playerIgnored = manager.getCache().getIgnorelist();
            playerIgnored.put(playeruuid, ignoredlist);
        }
    }
}
