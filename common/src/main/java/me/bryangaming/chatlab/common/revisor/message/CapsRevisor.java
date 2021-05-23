package me.bryangaming.chatlab.common.revisor.message;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;
import org.apache.commons.lang.StringUtils;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public class CapsRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public CapsRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }
    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor." + revisorName + ".enabled");
    }

    public String revisor(PlayerWrapper player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        int mayusmin = utils.getInt("revisor." + revisorName + ".min-mayus", -1);
        int mayuscount = 0;

        if (mayusmin < 0) {
            senderManager.sendMessage(player, "%p &fEmmm, you either didn't put anything in the config, or you are trying to detect invisible mayus?");
            senderManager.sendMessage(player, "EasterEgg #4");
        }

        for (char letter : string.toCharArray()) {

            if (Character.isUpperCase(letter)) {
                mayuscount++;
            }
        }

        if (mayuscount <= mayusmin) {
            return string;
        }

        if (utils.getBoolean("revisor." + revisorName + ".warning.enabled")) {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (senderManager.hasPermission(onlinePlayer, "revisor.watch")) {
                    senderManager.sendMessage(onlinePlayer, utils.getString("revisor." + revisorName + ".warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }

        return StringUtils.capitalize(string);
    }

}
