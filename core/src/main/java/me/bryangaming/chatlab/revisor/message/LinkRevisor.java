package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class LinkRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public LinkRevisor(PluginService pluginService, String revisorName) {
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

    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (utils.getBoolean("revisor." + revisorName + ".block-point")) {
            if (!string.contains(".")) {
                return string;
            }

            for (String word : string.split(" ")) {
                if (word.contains(".")) {
                    string = string.replace(word, utils.getString("revisor." + revisorName + ".replace-link"));
                    break;
                }
            }

            string = string.replace(".", utils.getString("revisor." + revisorName + ".replace-link"));
            sendMessage(player, " . ");

            if (!(player.isOnline())) {
                return null;
            }

            if (string.trim().isEmpty()) {
                return null;
            }

            return string;
        }


        List<String> blockList = utils.getStringList("revisor." + revisorName + ".blocked-links");

        for (String blockedWord : blockList) {
            for (String word : string.split(" ")) {
                if (!string.contains(blockedWord)) {
                    continue;
                }

                string = string.replace(word, utils.getString("revisor." + revisorName + ".replace-link"));
                sendMessage(player, blockedWord);

                if (!(player.isOnline())) {
                    return null;
                }

                if (string.trim().isEmpty()) {
                    return null;
                }

            }

        }

        return string;

    }

    private void sendMessage(Player player, String blockedword) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (utils.getBoolean("revisor." + revisorName + ".message.enabled")) {
            senderManager.sendMessage(player, utils.getString("revisor." + revisorName + ".message.format")
                    .replace("%player%", player.getName())
                    .replace("%blockedword%", blockedword));
        }

        if (utils.getBoolean("revisor." + revisorName + ".command.enabled")) {
            senderManager.sendCommand(Bukkit.getServer().getConsoleSender(), TextUtils.convertText(player, utils.getString("revisor." + revisorName + ".command.format")
                    .replace("%player%", player.getName())
                    .replace("%blockedword%", blockedword)));
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

}
