package atogesputo.bryangaming.chatlab.managers.player;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.string.StringUtils;
import atogesputo.bryangaming.chatlab.utils.string.VariableUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class PlayerStatic {

    private static Pattern pattern;
    private static Pattern rgBPattern;

    private static PlayerMessage playerMethod;

    private PluginService pluginService;

    public PlayerStatic(PluginService pluginService) {
        this.pluginService = pluginService;

        playerMethod = pluginService.getPlayerMethods().getSender();

    }

    public static String convertText(Player player, String path) {
        path = VariableUtils.replaceAllVariables(player, path);
        return StringUtils.setColor(path);
    }

    public static Component convertTextToComponent(Player player, String path) {
        path = VariableUtils.replaceAllVariables(player, path);
        path = StringUtils.convertLegacyToMiniMessage(path);

        return MiniMessage.get().parse(path);
    }

    public static Component convertTextToComponent(Player player, String path, String message) {
        path = VariableUtils.replaceAllVariables(player, path);
        path = StringUtils.convertLegacyToMiniMessage(path);

        if (!playerMethod.hasPermission(player, "color.variable")) {
            message = "<pre>" + message + "</pre>";
        }else{
            message = StringUtils.convertLegacyToMiniMessage(message);
        }

        path = path
                .replace("%message%", message);

        return MiniMessage.get().parse(path);
    }

    public static String convertText(Player player, String path, String message) {
        path = VariableUtils.replaceAllVariables(player, path);
        path = StringUtils.convertLegacyToMiniMessage(path);

        if (!playerMethod.hasPermission(player, "color.variable")) {
            message = "<pre>" + message + "</pre>";
        }else{
            message = StringUtils.convertLegacyToMiniMessage(message);
        }

        return path
                .replace("%message%", message);

    }




}
