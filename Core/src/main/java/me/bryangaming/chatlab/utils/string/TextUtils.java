package me.bryangaming.chatlab.utils.string;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class TextUtils {

    private PluginService pluginService;

    private static Configuration config;
    private static SenderManager senderManager;
    private static Logger logger;

    public TextUtils(PluginService pluginService) {
        this.pluginService = pluginService;

        config = pluginService.getFiles().getConfigFile();
        senderManager = pluginService.getPlayerManager().getSender();
        logger = pluginService.getPlugin().getLogger();
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

    public static boolean isNumber(String path) {
        return StringUtils.isNumeric(path);
    }

    public static boolean isNumberOr(String... paths) {
        for (String path : paths) {
            return StringUtils.isNumeric(path);
        }

        return false;
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

    public static void printStackTrace() {
        logger.info("Code line:");

        StackTraceElement[] stackTraceElement = new Exception().getStackTrace();

        for (int stackId = 0; stackId < stackTraceElement.length; stackId++) {

            if (stackId < 1) {
                continue;
            }

            String errorLine = stackTraceElement[stackId].toString();

            if (errorLine.contains("java.")) {
                break;
            }

            if (!errorLine.contains("code.")) {
                continue;
            }

            logger.info("- " + stackTraceElement[stackId].toString());

        }
    }
}
