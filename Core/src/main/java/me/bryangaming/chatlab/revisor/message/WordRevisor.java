package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordRevisor implements Revisor {

    private final PluginService pluginService;

    public WordRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public boolean isEnabled(){
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor.words-module.enabled");
    }

    @Override
    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();
        
        int words = 0;
        boolean bwstatus = false;

        Pattern pattern;
        for (String regex : utils.getStringList("revisor.words-module.list-words")) {

            if (!utils.getBoolean("revisor.words-module.regex")) {
                for (String wordsPath : string.split(" ")) {
                    if (wordsPath.equalsIgnoreCase(regex.split(";")[0])) {

                        string = string.replace(regex.split(";")[0], regex.split(";")[1]);

                        if (words < 1) {
                            sendProtocolMessage(player);
                        }

                        words++;
                    }
                }
                continue;
            }

            pattern = Pattern.compile(regex.split(";")[0]);
            Matcher matcher = pattern.matcher(string);

            while (matcher.find()) {
                String replaced = string.substring(matcher.start(), matcher.end());

                string = string.replace(replaced, regex.split(";")[1]);
                matcher = pattern.matcher(string);

                if (words < 1) {
                    sendProtocolMessage(player);
                }

                words++;
                bwstatus = true;
            }
        }

        if (bwstatus) {
            if (utils.getBoolean("revisor.words-module.word-list.enabled")) {
                playerMethod.sendMessage(player, utils.getString("revisor.words-module.word-list.format")
                        .replace("%words%", String.valueOf(words)));
            }
        }


        return string;
    }

    private void sendProtocolMessage(Player player){

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager playerMethod = pluginService.getPlayerManager().getSender();

            if (utils.getBoolean("revisor.words-module.message.enabled")) {
                playerMethod.sendMessage(player, utils.getString("revisor.words-module.message.format")
                        .replace("%player%", player.getName()));
            }

            if (utils.getBoolean("revisor.words-module.command.enabled")) {
                playerMethod.sendCommand(Bukkit.getConsoleSender(), TextUtils.convertText(player, utils.getString("revisor.words-module.command.format")
                        .replace("%player%", player.getName())));
            }

            if (utils.getBoolean("revisor.words-module.warning.enabled")) {
                Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                    if (playerMethod.hasPermission(onlinePlayer, "revisor.watch")) {
                        playerMethod.sendMessage(onlinePlayer, utils.getString("revisor.words-module.warning.text")
                                .replace("%player%", player.getName()));
                    }
                });
            }
    }
}
