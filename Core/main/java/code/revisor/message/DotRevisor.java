package code.revisor.message;

import code.PluginService;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DotRevisor {


    private PluginService pluginService;

    public DotRevisor(PluginService pluginService){
        this.pluginService = pluginService;
    }

    public String check(Player player, String string){

        Configuration utils = pluginService.getFiles().getBasicUtils();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        if (!(utils.getBoolean("revisor.dot-module.enabled"))){
            return string;
        }

        int lettermin = utils.getInt("revisor.dot-module.min-word");

        if (string.length() <= lettermin) {
            return string;
        }

        string = string + ".";

        if (!utils.getBoolean("revisor.dot-module.warning.enabled")) {
            return string;
        }

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            if (playerMethod.hasPermission(onlinePlayer, "revisor")){
                playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.dot-module.warning.text")
                        .replace("%player%", player.getName()));
            }
        });

        return string;
    }
}
