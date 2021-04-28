package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
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

    @Override
    public boolean isEnabled(){
        return pluginService.getFiles().getFormatsFile().getBoolean("filters.mention.enabled");
    }

    @Override
    public String revisor(Player player, String string) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        String[] listString = string.split(" ");

        String mentionTag = utils.getString("filters.mentions.char-tag");
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
            string = string.replace(mentionTag + playerMentioned.getName(), utils.getString("filters.mentions.replace-mention")
                    .replace("%player%", playerMentioned.getName()) + "&r");
            senderManager.sendMessage(playerMentioned, utils.getString("filters.mentions.message")
                    .replace("%player%", playerMentioned.getName()));
        }
        return string;
    }

}