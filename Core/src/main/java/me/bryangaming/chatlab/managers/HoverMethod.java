package atogesputo.bryangaming.chatlab.managers;

import atogesputo.bryangaming.chatlab.PluginService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import atogesputo.bryangaming.chatlab.managers.player.PlayerStatic;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;


public class HoverMethod {

    private final PluginService pluginService;

    public HoverMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public Component convertBaseComponent(Player player, String message) {

        MiniMessage miniMessage = MiniMessage.get();

        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();

        String playerRank = groupMethod.getPlayerGroup(player);
        Set<String> configSection = groupMethod.getConfigSection(player, playerRank);

        if (configSection == null) {
            pluginService.getPlugin().getLogger().info("Please put the bases format in the utils.yml");
            pluginService.getPlugin().getLogger().info("For more info, contact in the discord support.");
            return null;
        }

        Component component = miniMessage.parse("");

        for (String format : configSection) {

            String textPath = groupMethod.getPlayerFormat(player, playerRank, format);

            if (textPath.contains("%message%")) {
                textPath = PlayerStatic.convertText(player, textPath, message);
            } else {
                textPath = PlayerStatic.convertText(player, textPath);
            }

            Component newComponent = miniMessage.parse(textPath);

            List<String> textHover = groupMethod.getPlayerHover(player, playerRank, format);
            String textCommand = groupMethod.getPlayerCmd(player, playerRank, format);

            if (!textHover.isEmpty()) {
                textHover.replaceAll(string -> PlayerStatic.convertText(player, string));

                newComponent = newComponent.hoverEvent(HoverEvent.showText(miniMessage.parse(String.join("\n", textHover))));
                newComponent = newComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, textCommand
                        .replace("%player%", player.getName())));
            }

            component = component.append(newComponent);
        }

        return component;
    }

}
