package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;


public class HoverManager {

    private final PluginService pluginService;
    private final Configuration formatsFile;

    public HoverManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
    }


    public Component convertBaseComponent(Player player, String message) {

        MiniMessage miniMessage = MiniMessage.get();

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        String playerRank = groupManager.getPlayerGroup(player);
        String chatFormat = groupManager.getFormat(userData.getChannelType(), player, playerRank);

        if (formatsFile.getConfigurationSection(chatFormat + ".bases")== null) {
            pluginService.getPlugin().getLogger().info("Please put the bases format in the formats.yml");
            pluginService.getPlugin().getLogger().info("For more info, contact in the discord support.");
            return null;
        }

        Component component = miniMessage.parse("");

        for (String format : formatsFile.getConfigurationSection(chatFormat + ".bases").getKeys(false)){

            String textPath = formatsFile.getString(chatFormat + ".bases." + format + ".format");

            if (textPath.contains("%message%")) {
                textPath = TextUtils.convertText(player, textPath, message);
            } else {
                textPath = TextUtils.convertText(player, textPath);
            }

            Component newComponent = miniMessage.parse(textPath);

            List<String> textHover = formatsFile.getStringList(chatFormat + ".bases." + format + ".hover");

            String textType = formatsFile.getString(chatFormat + ".bases." + format + ".action.type");
            String textCommand = formatsFile.getString(chatFormat + ".bases." + format + ".action.format");

            if (!textHover.isEmpty()) {
                textHover.replaceAll(string -> TextUtils.convertText(player, string));

                newComponent = newComponent.hoverEvent(HoverEvent.showText(miniMessage.parse(String.join("\n", textHover))));
                newComponent = newComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.valueOf(textType), textCommand
                        .replace("%player%", player.getName())));
            }

            component = component.append(newComponent);
        }

        return component;
    }

}
