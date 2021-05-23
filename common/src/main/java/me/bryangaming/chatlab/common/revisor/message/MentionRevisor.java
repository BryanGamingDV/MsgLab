package me.bryangaming.chatlab.common.revisor.message;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.ArrayList;
import java.util.List;

public class MentionRevisor implements Revisor {

    private final PluginService pluginService;
    private final String revisorName;

    public MentionRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled(){
        return pluginService.getFiles().getFormatsFile().getBoolean("filters." + revisorName +  ".enabled");
    }

    @Override
    public String revisor(PlayerWrapper player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        String[] listString = string.split(" ");

        String mentionTag = utils.getString("filters." + revisorName + ".char-tag");
        List<PlayerWrapper> playersMentioned = new ArrayList<>();

        for (String path : listString) {
            if (path.startsWith(mentionTag)) {

                String playerMentionedName = path.substring(mentionTag.length());
                PlayerWrapper playerMentioned = Bukkit.getPlayerExact(playerMentionedName);

                if (playerMentioned == null) {
                    continue;
                }

                playersMentioned.add(playerMentioned);
            }
        }

        if (playersMentioned.isEmpty()) {
            return string;
        }

        for (PlayerWrapper playerMentioned : playersMentioned) {
            string = string.replace(mentionTag + playerMentioned.getName(), utils.getString("filters." + revisorName + ".replace-mention")
                    .replace("%player%", playerMentioned.getName()) + "&r");
            senderManager.sendMessage(playerMentioned, utils.getString("filters." + revisorName + ".message")
                    .replace("%player%", playerMentioned.getName()));
        }
        return string;
    }

}