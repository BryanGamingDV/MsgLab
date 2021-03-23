package code.revisor.message;

import code.PluginService;
import code.bukkitutils.RunnableManager;
import code.managers.player.PlayerMessage;
import code.managers.player.PlayerStatic;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordRevisor {

    private PluginService pluginService;

    private RunnableManager runnableManager;

    public WordRevisor(PluginService pluginService) {
        this.pluginService = pluginService;

        this.runnableManager = pluginService.getManagingCenter().getRunnableManager();
    }

    public String check(Player player, String string) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        if (!(utils.getBoolean("revisor.words-module.enabled"))) {
            return string;
        }

        List<String> badwordslist;

        if (!utils.getConfigurationSection("revisor.words-module.list-words").getKeys(false).isEmpty() || utils.getConfigurationSection("revisor.bad-words.list-words") != null) {
            badwordslist = new ArrayList<>(utils.getConfigurationSection("revisor.words-module.list-words").getKeys(false));
        } else {
            badwordslist = utils.getStringList("revisor.words-module.list-words");
        }

        int words = 0;
        boolean bwstatus = false;

        Pattern pattern;
        for (String regex : utils.getStringList("revisor.words-module.regex")){
            pattern = Pattern.compile(regex.split(";")[0]);

            Matcher matcher = pattern.matcher(string);

            while (matcher.find()){
                String replaced = string.substring(matcher.start(), matcher.end());

                string = string.replace(replaced, regex.split(";")[1]);
                matcher = pattern.matcher(string);
            }
        }

        for (String badword : badwordslist) {
            if (!string.contains(badword)) {
                continue;
            }

            if (words < 1) {
                if (utils.getBoolean("revisor.words-module.message.enabled")) {
                    playerMethod.sendMessage(player, utils.getString("revisor.words-module.message.format")
                            .replace("%player%", player.getName()));
                }

                if (utils.getBoolean("revisor.words-module.command.enabled")) {
                    runnableManager.sendCommand(Bukkit.getConsoleSender(), PlayerStatic.setVariables(player, utils.getString("revisor.words-module.command.format")
                            .replace("%player%", player.getName())));
                }
                bwstatus = true;

                if (utils.getBoolean("revisor.words-module.warning.enabled")) {
                    Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                        if (playerMethod.hasPermission(onlinePlayer, "revisor")) {
                            playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.words-module.warning.text")
                                    .replace("%player%", player.getName()));
                        }
                    });
                }
            }

            if (utils.getString("revisor.words-module.word-replaced") != null) {
                string = string.replace(badword, utils.getString("revisor.words-module.word-replaced"));
            } else {
                string = string.replace(badword, utils.getString("revisor.words-module.list-words." + badword));
            }

            words++;
        }

        if (bwstatus) {
            if (utils.getBoolean("revisor.words-module.word-list.enabled")) {
                playerMethod.sendMessage(player, utils.getString("revisor.words-module.word-list.format")
                        .replace("%words%", String.valueOf(words)));
            }
        }


        return string;
    }
}
