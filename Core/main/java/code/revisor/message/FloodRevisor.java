package code.revisor.message;

import code.Manager;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FloodRevisor {

    private Manager manager;

    public static final String LETTERS = "AaBbCcDdEeFfGgHhIiJjKkMmNnLlOoPpQqRrSsTtUuVvWwXxYyZz0123456789";

    public FloodRevisor(Manager manager){
        this.manager = manager;
    }

    public String check(Player player, String string){

        Configuration config = manager.getFiles().getConfig();
        Configuration utils = manager.getFiles().getBasicUtils();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        if (!(utils.getBoolean("utils.chat.security.anti-flood.enabled"))){
            return string;
        }


        int floodstatus = 0;
        int minflood = Math.max(0, utils.getInt("utils.chat.security.anti-flood.min-chars"));

        for (char letter : LETTERS.toCharArray()){

            for(int count = 50; count > minflood; count--){

                String letterchanged = String.valueOf(letter);

                if (string.contains(Strings.repeat(letterchanged, count))) {

                    if (floodstatus < 1) {
                        playersender.sendMessage(player, utils.getString("utils.chat.security.anti-flood.message"));

                        if (!utils.getBoolean("utils.chat.security.anti-flood.warning.enabled")) {
                            continue;
                        }

                        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                            if (onlinePlayer.hasPermission(config.getString("config.perms.revisor-watch"))) {
                                playersender.sendMessage(onlinePlayer, utils.getString("utils.chat.security.anti-flood.warning.text")
                                        .replace("%player%", player.getName()));
                            }
                        });
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
