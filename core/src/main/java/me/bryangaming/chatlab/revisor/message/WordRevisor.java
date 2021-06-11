package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.List;
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
        return pluginService.getFiles().getFiltersFile().getBoolean("message." + revisorName + ".enabled");
    }

    @Override
    public String revisor(Player player, String string) {

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        
        int words = 0;
        boolean bwstatus = false;

        Pattern pattern;
        for (String regex : filtersFile.getStringList("message." + revisorName + ".list-words")) {

            if (!filtersFile.getBoolean("message.words-module.regex")) {
                for (String wordsPath : string.split(" ")) {
                    if (wordsPath.equalsIgnoreCase(regex.split(";")[0])) {

                        string = string.replace(regex.split(";")[0], regex.split(";")[1]);

                        if (words < 1) {
                            sendProtocolMessage(player);
                        }

                        if (filtersFile.getBoolean("message." + revisorName + ".cancel-message")){
                            return null;
                        }
                        words++;
                    }
                }
                continue;
            }

            pattern = Pattern.compile(regex.split(";")[0]);
            Matcher matcher = pattern.matcher(string);

            while (matcher.find()) {
                if (words < 1) {
                    sendProtocolMessage(player);
                }
                if (filtersFile.getBoolean("message." + revisorName + ".cancel-message")){
                    return null;
                }

                String replaced = string.substring(matcher.start(), matcher.end());

                string = string.replace(replaced, regex.split(";")[1]);
                matcher = pattern.matcher(string);
                words++;
                bwstatus = true;
            }
        }

        if (bwstatus) {
            if (filtersFile.getBoolean("message." + revisorName + ".word-list.enabled")) {
                senderManager.sendMessage(player, filtersFile.getString("message." + revisorName + ".word-list.format")
                        .replace("%words%", String.valueOf(words)));
            }
        }


        return string;
    }

    private void sendProtocolMessage(Player player){

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();

        ActionManager actionManager = pluginService.getPlayerManager().getActionManager();
        List<String> listActions = filtersFile.getStringList("message." + revisorName + ".actions");

        if (!listActions.isEmpty()) {
            actionManager.execute(player, listActions);
        }
    }
}
