package me.bryangaming.chatlab.utils.string;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TextUtils {

    private PluginService pluginService;
    private static Configuration config;
    private static SenderManager senderManager;

    public TextUtils(PluginService pluginService) {
        this.pluginService = pluginService;

        config = pluginService.getFiles().getConfig();
        senderManager = pluginService.getPlayerManager().getSender();
    }

    public static String setColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
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

        if (!senderManager.hasPermission(player, "color.variable")) {
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

        if (!senderManager.hasPermission(player, "color.variable")) {
            message = "<pre>" + message + "</pre>";
        } else {
            message = TextUtils.convertLegacyToMiniMessage(message);
        }

        return path
                .replace("%message%", message);

    }



    public static String convertLegacyToMiniMessage(String string) {

        if (!config.getBoolean("options.use-legacy-colors")) {
            return string;
        }
        string = string
                .replace("&f", "<white>");

        return MiniMessage.get().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(string).asComponent());
    }

    public static void addToList(List<String> arrayList, String... listValues){
        arrayList.addAll(Arrays.asList(listValues));
    }

    public static String getUsage(String command, String... args) {

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            if (!(arg.contains(","))) {
                message.append(arg).append(" ");
                continue;
            }

            message.append("[").append(arg).append("] ");
        }

        return "/" + command + " " + message.toString();

    }
}
