package atogesputo.bryangaming.chatlab.managers.chat;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RadialChatMethod {


    private final PluginService pluginService;

    public RadialChatMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public List<Player> getRadialPlayers(Player player) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        int x = utils.getInt("radial-chat.x");
        int y = utils.getInt("radial-chat.y");
        int z = utils.getInt("radial-chat.z");

        ArrayList<Player> listPlayers = new ArrayList<>();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
            @Override
            public void run() {

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), x, y, z)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }

                    Player online = (Player) entity;

                    if (online.getName().equalsIgnoreCase(player.getName())) {
                        continue;
                    }

                    listPlayers.add(online);

                }

            }
        }, 0);
        return listPlayers;
    }
}
