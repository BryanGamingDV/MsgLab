package code.revisor.message;

import code.Manager;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CapsRevisor {

    private Manager manager;

    public CapsRevisor(Manager manager){
        this.manager = manager;

    }

    public String check(Player player, String string){

        Configuration config = manager.getFiles().getConfig();
        Configuration utils = manager.getFiles().getBasicUtils();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        if (!(utils.getBoolean("utils.chat.security.caps-module.enabled"))){
            return string;
        }

        int mayusmin = utils.getInt("utils.chat.security.caps-module.min-mayus", -1);
        int mayuscount = 0;

        if (mayusmin < 0){
            playersender.sendMessage(player, "%p &fEmmm, or you didn't put anything in the config, or you are trying to detect invisible mayus?");
            playersender.sendMessage(player, "EasterEgg #4");
        }

        for(char letter : string.toCharArray()){

            if (Character.isUpperCase(letter)){
                mayuscount++;
            }
        }

        if (mayuscount <= mayusmin){
            return string;
        }

        if (!utils.getBoolean("utils.chat.security.caps-module.warning.enabled")) {
            return StringUtils.capitalize(string);
        }

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.hasPermission(config.getString("config.perms.revisor-watch"))){
                playersender.sendMessage(onlinePlayer, utils.getString("utils.chat.security.caps-module.warning.text")
                        .replace("%player%", player.getName()));
            }
        });

        return StringUtils.capitalize(string);
    }
}
