package me.bryangaming.chatlab.revisor.message;

import com.google.common.base.Strings;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FloodRevisor implements Revisor {

    public static final String LETTERS = "" + "AaBbCcDdEeFfGgHhIiJjKkMmNnLlOoPpQqRrSsTtUuVvWwXxYyZz0123456789";

    private final PluginService pluginService;
    private final String revisorName;

    public FloodRevisor(PluginService pluginService, String revisorName) {
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

    @Override
    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        int floodstatus = 0;
        int minflood = Math.max(0, utils.getInt("revisor." + revisorName + ".min-chars"));

        for (char letter : LETTERS.toCharArray()) {

            for (int count = 50; count > minflood; count--) {

                String letterchanged = String.valueOf(letter);

                if (string.contains(Strings.repeat(letterchanged, count))) {

                    if (floodstatus < 1) {
                        if (utils.getBoolean("revisor." + revisorName + ".message.enabled")) {
                            senderManager.sendMessage(player, utils.getString("revisor." + revisorName + ".message.format"));
                        }

                        if (utils.getBoolean("revisor." + revisorName + ".warning.enabled")) {
                            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                                if (senderManager.hasPermission(onlinePlayer, "revisor.watch")) {
                                    senderManager.sendMessage(onlinePlayer, utils.getString("revisor." + revisorName + ".warning.text")
                                            .replace("%player%", player.getName()));
                                }
                            });
                        }
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
