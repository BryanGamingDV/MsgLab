package atogesputo.bryangaming.chatlab.revisor.message;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import org.apache.commons.lang.StringUtils;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CapsRevisor {

    private PluginService pluginService;

    public CapsRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String check(Player player, String string) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        if (!(utils.getBoolean("revisor.caps-module.enabled"))) {
            return string;
        }

        int mayusmin = utils.getInt("revisor.caps-module.min-mayus", -1);
        int mayuscount = 0;

        if (mayusmin < 0) {
            playerMethod.sendMessage(player, "%p &fEmmm, you either didn't put anything in the config, or you are trying to detect invisible mayus?");
            playerMethod.sendMessage(player, "EasterEgg #4");
        }

        for (char letter : string.toCharArray()) {

            if (Character.isUpperCase(letter)) {
                mayuscount++;
            }
        }

        if (mayuscount <= mayusmin) {
            return string;
        }

        if (utils.getBoolean("revisor.caps-module.warning.enabled")) {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (playerMethod.hasPermission(onlinePlayer, "revisor")) {
                    playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.caps-module.warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return StringUtils.capitalize(string);
    }
}
