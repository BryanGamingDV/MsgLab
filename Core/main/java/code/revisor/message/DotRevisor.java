package code.revisor.message;

import code.Manager;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DotRevisor {


    private Manager manager;

    public DotRevisor(Manager manager){
        this.manager = manager;
    }

    public String check(Player player, String string){

        Configuration config = manager.getFiles().getConfig();
        Configuration utils = manager.getFiles().getBasicUtils();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        if (!(utils.getBoolean("utils.chat.security.dot-module.enabled"))){
            return string;
        }

        int lettermin = utils.getInt("utils.chat.security.dot-module.min-word");

        if (string.length() <= lettermin) {
            return string;
        }

        string = string + ".";

        if (!utils.getBoolean("utils.chat.security.dot-module.warning.enabled")) {
            return string;
        }

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.hasPermission(config.getString("config.perms.revisor-watch"))){
                playersender.sendMessage(onlinePlayer, utils.getString("utils.chat.security.dot-module.warning.text")
                        .replace("%player%", player.getName()));
            }
        });

        return string;
    }
}
