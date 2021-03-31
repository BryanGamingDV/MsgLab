package code.revisor.message;

import code.PluginService;
import code.bukkitutils.RunnableManager;
import code.managers.player.PlayerMessage;
import code.managers.player.PlayerStatic;
import code.utils.Configuration;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class LinkRevisor {

    private final PluginService pluginService;

    private RunnableManager runnableManager;

    public LinkRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
        this.runnableManager = pluginService.getManagingCenter().getRunnableManager();

    }

    public String check(Player player, String string) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!(utils.getBoolean("revisor.link-module.enabled"))) {
            return string;
        }

        if (utils.getBoolean("revisor.link-module.block-point")) {
            if (string.contains(".")) {
                for (String word : string.split(" ")) {
                    if (word.contains(".")) {
                        string = string.replace(word, utils.getString("revisor.link-module.replace-link"));
                        break;
                    }
                }
                string = string.replace(".", utils.getString("revisor.link-module.replace-link"));
                sendMessage(player, " . ");

                if (!(player.isOnline())) {
                    return null;
                }

                if (string.trim().isEmpty()) {
                    return null;
                }

                return string;
            }

            return string;
        }


        List<String> blockList = utils.getStringList("revisor.link-module.blocked-links");

        for (String blockedWord : blockList) {
            if (string.contains(blockedWord)) {
                string = string.replace(".", utils.getString("revisor.link-module.replace-link"));
                sendMessage(player, blockedWord);

                if (!(player.isOnline())) {
                    return null;
                }

                if (string.trim().isEmpty()) {
                    return null;
                }

                return string;
            }

        }

        return string;

    }

    private void sendMessage(Player player, String blockedword) {

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("revisor.link-module.message.enabled")) {
            playerMethod.sendMessage(player, utils.getString("revisor.link-module.message.format")
                    .replace("%player%", player.getName())
                    .replace("%blockedword%", blockedword));
        }

        if (utils.getBoolean("revisor.link-module.command.enabled")) {
            runnableManager.sendCommand(Bukkit.getServer().getConsoleSender(), PlayerStatic.convertText(player, utils.getString("revisor.link-module.command.format")
                    .replace("%player%", player.getName())
                    .replace("%blockedword%", blockedword)));
        }

        if (utils.getBoolean("revisor.link-module.warning.enabled")) {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (playerMethod.hasPermission(onlinePlayer, "revisor")) {
                    playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.link-module.warning.text")
                            .replace("%player%", player.getName()));
                }
            });
        }
    }

}
