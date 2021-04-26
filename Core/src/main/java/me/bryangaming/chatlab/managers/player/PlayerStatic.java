package me.bryangaming.chatlab.managers.player;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.bryangaming.chatlab.utils.string.VariableUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PlayerStatic {

    private static SenderManager playerMethod;

    private PluginService pluginService;

    public PlayerStatic(PluginService pluginService) {
        this.pluginService = pluginService;

        playerMethod = pluginService.getPlayerManager().getSender();

    }

    public static String convertText(Player player, String path) {
        path = VariableUtils.replaceAllVariables(player, path);
        return TextUtils.setColor(path);
    }

    public static Component convertTextToComponent(Player player, String path) {
        path = VariableUtils.replaceAllVariables(player, path);
        path = TextUtils.convertLegacyToMiniMessage(path);

        return MiniMessage.get().parse(path);
    }

    public static Component convertTextToComponent(Player player, String path, String message) {
        path = VariableUtils.replaceAllVariables(player, path);
        path = TextUtils.convertLegacyToMiniMessage(path);

        if (!playerMethod.hasPermission(player, "color.variable")) {
            message = "<pre>" + message + "</pre>";
        } else {
            message = TextUtils.convertLegacyToMiniMessage(message);
        }

        path = path
                .replace("%message%", message);

        return MiniMessage.get().parse(path);
    }

    public static String convertText(Player player, String path, String message) {
        path = VariableUtils.replaceAllVariables(player, path);
        path = TextUtils.convertLegacyToMiniMessage(path);

        if (!playerMethod.hasPermission(player, "color.variable")) {
            message = "<pre>" + message + "</pre>";
        } else {
            message = TextUtils.convertLegacyToMiniMessage(message);
        }

        return path
                .replace("%message%", message);

    }


}
