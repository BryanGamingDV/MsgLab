package code.revisor.message;

import code.Manager;
import code.bukkitutils.RunnableManager;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class WordRevisor {

    private Manager manager;

    private RunnableManager runnableManager;

    public WordRevisor(Manager manager) {
        this.manager = manager;

        this.runnableManager = manager.getManagingCenter().getRunnableManager();
    }

    public String check(Player player, String string) {

        Configuration config = manager.getFiles().getConfig();
        Configuration utils = manager.getFiles().getBasicUtils();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        if (!(utils.getBoolean("utils.chat.security.bad-words.enabled"))) {
            return string;
        }

        List<String> badwordslist = utils.getStringList("utils.chat.security.bad-words.list-words");

        int words = 0;
        boolean bwstatus = false;

        for (String badword : badwordslist) {
            if (string.contains(badword)) {
                if (words < 1) {
                    playersender.sendMessage(player, utils.getString("utils.chat.security.bad-words.message")
                            .replace("%player%", player.getName()));

                    if (utils.getBoolean("utils.chat.security.bad-words.command.enabled")){
                        runnableManager.sendCommand(Bukkit.getConsoleSender(), PlayerStatic.setFormat(player, utils.getString("utils.chat.security.bad-words.command.format")
                                .replace("%player%", player.getName())));
                    }
                    bwstatus = true;

                    if (!utils.getBoolean("utils.chat.security.bad-words.warning.enabled")) {
                        continue;
                    }

                    Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                        if (onlinePlayer.hasPermission(config.getString("config.perms.revisor-watch"))){
                            playersender.sendMessage(onlinePlayer, utils.getString("utils.chat.security.bad-words.warning.text")
                                    .replace("%player%", player.getName()));

                        }
                    });

                }

                words++;
            }

            string = string.replace(badword, utils.getString("utils.chat.security.bad-words.word-replaced"));
        }

        if (bwstatus) {
            if (utils.getBoolean("utils.chat.security.bad-words.word-list.enabled")) {
                playersender.sendMessage(player, utils.getString("utils.chat.security.bad-words.word-list.format")
                        .replace("%words%", String.valueOf(words)));
            }

        }



        return string;
    }
}
