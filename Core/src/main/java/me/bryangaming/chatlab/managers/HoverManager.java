package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.managers.group.GroupMethod;
import me.bryangaming.chatlab.utils.string.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;


public class HoverManager {

    private final PluginService pluginService;

    public HoverManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public Component convertBaseComponent(Player player, String message) {

        MiniMessage miniMessage = MiniMessage.get();

        GroupMethod groupMethod = pluginService.getPlayerManager().getGroupMethod();
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        GroupEnum channelType = userData.getChannelType();

        String playerRank = groupMethod.getPlayerGroup(player);
        Set<String> configSection = groupMethod.getConfigSection(channelType, player, playerRank);

        if (configSection == null) {
            pluginService.getPlugin().getLogger().info("Please put the bases format in the formats.yml");
            pluginService.getPlugin().getLogger().info("For more info, contact in the discord support.");
            return null;
        }

        Component component = miniMessage.parse("");

        for (String format : configSection) {

            String textPath = groupMethod.getPlayerFormat(channelType, player, playerRank, format);

            if (textPath.contains("%message%")) {
                textPath = TextUtils.convertText(player, textPath, message);
            } else {
                textPath = TextUtils.convertText(player, textPath);
            }

            Component newComponent = miniMessage.parse(textPath);

            List<String> textHover = groupMethod.getPlayerHover(channelType, player, playerRank, format);

            String textType = groupMethod.getPlayerActionType(channelType, player, playerRank, format);
            String textCommand = groupMethod.getPlayerActionFormat(channelType, player, playerRank, format);

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
