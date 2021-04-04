package atogesputo.bryangaming.chatlab.revisor.message;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MentionRevisor {

    private PluginService pluginService;

    public MentionRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String check(String string) {

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        Configuration utils = pluginService.getFiles().getBasicUtils();

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