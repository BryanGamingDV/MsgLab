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

        Configuration config = pluginService.getFiles().getConfig();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

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
