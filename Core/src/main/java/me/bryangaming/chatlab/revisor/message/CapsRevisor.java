package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CapsRevisor implements Revisor {

    private PluginService pluginService;

    public CapsRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor.caps-module.enabled");
    }

    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

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
                if (playerMethod.hasPermission(onlinePlayer, "revisor.watch")) {
                    playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.caps-module.warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return StringUtils.capitalize(string);
    }

}
