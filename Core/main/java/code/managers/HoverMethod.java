package code.managers;

import code.PluginService;
import code.managers.player.PlayerStatic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;


public class HoverMethod {

    private final PluginService pluginService;

    public HoverMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public Component convertBaseComponent(Player player, String message) {

        MiniMessage miniMessage = MiniMessage.get();

        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();

        if (groupMethod.getConfigSection(player) == null) {
            pluginService.getPlugin().getLogger().info("Please put the bases format in the utils.yml");
            pluginService.getPlugin().getLogger().info("For more info, contact in the discord support.");
            return null;
        }

        Component component = miniMessage.parse("");

        for (String format : groupMethod.getConfigSection(player)) {

            String textPath = groupMethod.getPlayerFormat(player, format);

            if (textPath.contains("%message%")) {
                textPath = PlayerStatic.setFormat(player, textPath, message);
            } else {
                textPath = PlayerStatic.setFormat(player, textPath);
            }

            Component newComponent = miniMessage.parse(textPath);

            List<String> textHover = groupMethod.getPlayerHover(player, format);
            String textCommand = groupMethod.getPlayerCmd(player, format);

            if (!textHover.isEmpty()) {
                textHover = PlayerStatic.setFormatList(player, textHover);
                newComponent = newComponent.hoverEvent(HoverEvent.showText(miniMessage.parse(PlayerStatic.listToString(textHover))));
                newComponent = newComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, textCommand
                        .replace("%player%", player.getName())));
            }

            component = component.append(newComponent);
        }

        return component;
    }

}
