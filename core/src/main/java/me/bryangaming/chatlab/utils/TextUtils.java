package me.bryangaming.chatlab.utils;

import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class TextUtils {


    private static ChatLab chatLab;
    private static Configuration config;
    private static SenderManager senderManager;
    private static Logger logger;

    public TextUtils(PluginService pluginService) {
        chatLab = pluginService.getPlugin();
        config = pluginService.getFiles().getConfigFile();
        senderManager = pluginService.getPlayerManager().getSender();
        logger = pluginService.getPlugin().getLogger();
    }
    public static String setColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String convertText(Player player, String path) {
        path = PlaceholderUtils.replaceAllVariables(player, path);
        return TextUtils.setColor(path);
    }

    public static Component convertTextToComponent(Player player, String path) {
        path = PlaceholderUtils.replaceAllVariables(player, path);
        path = TextUtils.convertLegacyToMiniMessage(path);


        return MiniMessage.get().parse(path);
    }

    public static Component convertTextToComponent(Player player, String path, String message) {


        if (!senderManager.hasPermission(player, "chat-format", "color")){
            message = "<pre>" + message + "</pre>";
        } else {
            message = TextUtils.convertLegacyToMiniMessage(message);
        }

        path = path
                .replace("%message%", message);

        path = PlaceholderUtils.replaceAllVariables(player, path);
        path = TextUtils.convertLegacyToMiniMessage(path);

        return MiniMessage.get().parse(path);
    }

    public static String convertText(Player player, String path, String message) {

        if (!senderManager.hasPermission(player, "chat-format", "color")){
            message = "<pre>" + message + "</pre>";
        } else {
            message = TextUtils.convertLegacyToMiniMessage(message);
        }

        path = path
                .replace("%message%", message);

        path = PlaceholderUtils.replaceAllVariables(player, path);
        path = TextUtils.convertLegacyToMiniMessage(path);

        return path;

    }



    public static String convertLegacyToMiniMessage(String string) {

        if (!config.getBoolean("options.use-legacy-colors")) {
            return string;
        }
        string = string
                .replace("&f", "<white>");

        return MiniMessage.get().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(string).asComponent());
    }


    public static int countRepeatedCharacters(String string, char character) {

        int counted = 0;
        int count = 0;

        while (string.indexOf(character, count) != -1) {

            counted = string.indexOf(character, counted + 1);
            count++;
        }

        return count;
    }
    public static String getServerVersion(Server server){
        String version = server.getClass().getPackage().getName().split("\\.")[3];
        return version.replace("_", ".").substring(1, version.length() - 3);
    }

    public static boolean equalsIgnoreCaseOr(String string, String... targets) {
        for (String target : targets) {
            if (string.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
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
    public static boolean isHookEnabled(String pluginName){
        return config.getBoolean("options.allow-hooks." + pluginName.toLowerCase());
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
        logger.info("Plugin version: " + chatLab.getDescription().getVersion());
        logger.info("Bukkit version: " + getServerVersion(Bukkit.getServer()));
        logger.info("Code line:");

        StackTraceElement[] stackTraceElement = new Exception().getStackTrace();

        for (int stackId = 0; stackId < stackTraceElement.length; stackId++) {
            if (stackId < 1) {
                continue;
            }

            String errorLine = stackTraceElement[stackId].toString();

            if (errorLine.contains("sun.")) {
                break;
            }

            if (!errorLine.startsWith("me.bryangaming.chatlab")) {
                continue;
            }

            logger.info("- " + stackTraceElement[stackId].toString());

        }
    }
}
