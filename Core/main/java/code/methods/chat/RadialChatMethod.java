package code.methods.chat;

import code.Manager;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;

public class RadialChatMethod {


    private final Manager manager;

    public RadialChatMethod(Manager manager){
        this.manager = manager;
    }


    public List<Player> getRadialPlayers(Player player){
        Configuration utils = manager.getFiles().getBasicUtils();

        int x = utils.getInt("utils.chat.radial-chat.x");
        int y = utils.getInt("utils.chat.radial-chat.y");
        int z = utils.getInt("utils.chat.radial-chat.z");

        ArrayList<Player> listPlayers = new ArrayList<>();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(manager.getPlugin(), new Runnable() {
            @Override
            public void run() {

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), x, y , z)){
                    if (!(entity instanceof Player)){
                        continue;
                    }

                    Player online = (Player) entity;

                    if (online.getName().equalsIgnoreCase(player.getName())){
                        continue;
                    }

                    listPlayers.add(online);

                }

            }
        },0);
        return listPlayers;
    }
}
