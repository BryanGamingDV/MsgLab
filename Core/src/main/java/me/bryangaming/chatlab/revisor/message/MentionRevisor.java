package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MentionRevisor implements Revisor {

    private final PluginService pluginService;

    public MentionRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

        if (!(utils.getBoolean("fitlers.mentions.enabled"))) {
            return string;
        }

        String[] listString = string.split(" ");

        String mentionTag = utils.getString("fitlers.mentions.char-tag");
        List<Player> playersMentioned = new ArrayList<>();

        for (String path : listString) {
            if (path.startsWith(mentionTag)) {

                String playerMentionedName = path.substring(mentionTag.length());
                Player playerMentioned = Bukkit.getPlayerExact(playerMentionedName);

                if (playerMentioned == null) {
                    continue;
                }

                playersMentioned.add(playerMentioned);
            }
        }

        if (playersMentioned.isEmpty()) {
            return string;
        }

        for (Player playerMentioned : playersMentioned) {
            string = string.replace(mentionTag + playerMentioned.getName(), utils.getString("fitlers.mentions.replace-mention")
                    .replace("%player%", playerMentioned.getName()) + "&r");
            playersender.sendMessage(playerMentioned, utils.getString("fitlers.mentions.message")
                    .replace("%player%", playerMentioned.getName()));
        }
        return string;
    }

}