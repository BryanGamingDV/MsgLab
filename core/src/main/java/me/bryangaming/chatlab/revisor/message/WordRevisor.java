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
    private final String revisorName;

    public WordRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled(){
        return pluginService.getFiles().getFormatsFile().getBoolean("revisor." + revisorName + ".enabled");
    }

    @Override
    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        
        int words = 0;
        boolean bwstatus = false;

        Pattern pattern;
        for (String regex : utils.getStringList("revisor." + revisorName + ".list-words")) {

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
            if (utils.getBoolean("revisor." + revisorName + ".word-list.enabled")) {
                senderManager.sendMessage(player, utils.getString("revisor." + revisorName + ".word-list.format")
                        .replace("%words%", String.valueOf(words)));
            }
        }


        return string;
    }

    private void sendProtocolMessage(Player player){

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

            if (utils.getBoolean("revisor." + revisorName + ".message.enabled")) {
                senderManager.sendMessage(player, utils.getString("revisor." + revisorName + ".message.format")
                        .replace("%player%", player.getName()));
            }

            if (utils.getBoolean("revisor." + revisorName + ".command.enabled")) {
                senderManager.sendCommand(Bukkit.getConsoleSender(), TextUtils.convertText(player, utils.getString("revisor." + revisorName + ".command.format")
                        .replace("%player%", player.getName())));
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
