package code.revisor.message;

import code.Manager;
import code.bukkitutils.RunnableManager;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class LinkRevisor {

    private final Manager manager;

    private  RunnableManager runnableManager;

    public LinkRevisor(Manager manager){
        this.manager = manager;
        this.runnableManager = manager.getManagingCenter().getRunnableManager();

    }

    public String check(Player player, String string){

        Configuration utils = manager.getFiles().getBasicUtils();

        if (!(utils.getBoolean("utils.chat.security.link-module.enabled"))){
            return string;
        }

        if (utils.getBoolean("utils.chat.security.link-module.block-point")){
            if (string.contains(".")){
                for (String word : string.split(" ")){
                    if (word.contains(".")){
                        string = string.replace(word, utils.getString("utils.chat.security.link-module.replace-link"));
                        break;
                    }
                }
                string = string.replace(".", utils.getString("utils.chat.security.link-module.replace-link"));
                sendMessage(player, " . ");

                if (!(player.isOnline())){
                    return null;
                }

                if (string.trim().isEmpty()){
                    return null;
                }

                return string;
            }

            return string;
        }


        List<String> blockList = utils.getStringList("utils.chat.security.link-module.blocked-links");

        for (String blockedWord : blockList){
            if (string.contains(blockedWord)){
                string = string.replace(".", utils.getString("utils.chat.security.link-module.replace-link"));
                sendMessage(player, blockedWord);

                if (!(player.isOnline())){
                    return null;
                }

                if (string.trim().isEmpty()){
                    return null;
                }

                return string;
            }

        }

        return string;

    }

    private void sendMessage(Player player, String blockedword) {

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        Configuration config = manager.getFiles().getConfig();
        Configuration utils = manager.getFiles().getBasicUtils();

        playersender.sendMessage(player, utils.getString("utils.chat.security.link-module.message")
                .replace("%player%", player.getName())
                .replace("%blockedword%", blockedword));

        if (!(utils.getBoolean("utils.chat.security.link-module.command.enabled"))) {
            return;
        }

        runnableManager.sendCommand(Bukkit.getServer().getConsoleSender(), PlayerStatic.setFormat(player, utils.getString("utils.chat.security.link-module.command.format")
                    .replace("%player%", player.getName())
                    .replace("%blockedword%", blockedword)));

        if (!utils.getBoolean("utils.chat.security.link-module.warning.enabled")) {
            return;
        }

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.hasPermission(config.getString("config.perms.revisor-watch"))){
                playersender.sendMessage(onlinePlayer, utils.getString("utils.chat.security.link-module.warning.text")
                        .replace("%player%", player.getName()));
            }
        });
    }

}
