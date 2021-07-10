package me.bryangaming.chatlab.utils.text;

import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.PlaceholderUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.RGBLike;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {


    private static ChatLab chatLab;

    private static Configuration config;
    private static Configuration formatsFile;

    private static Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");

    private static SenderManager senderManager;
    private static Logger logger;

    public TextUtils(PluginService pluginService) {
        chatLab = pluginService.getPlugin();
        config = pluginService.getFiles().getConfigFile();
        formatsFile = pluginService.getFiles().getFormatsFile();
        senderManager = pluginService.getPlayerManager().getSender();
        logger = pluginService.getPlugin().getLogger();
    }
    public static String setColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String convertText(Player player, String path) {
        String formattedPath = PlaceholderUtils.replaceAllVariables(player, path);
        return TextUtils.setColor(formattedPath);
    }

    public static Component convertTextToComponent(Player player, String path) {
        String formattedPath = PlaceholderUtils.replaceAllVariables(player, path);
        formattedPath = TextUtils.convertLegacyToMiniMessage(formattedPath);


        return MiniMessage.get().parse(formattedPath);
    }

    public static String convertAliasesToCommand(String command){
        switch (command){
            case "acc":
                return "announcer";
            case "bc":
                return "broadcast";
            case "bcworld":
            case "bcw":
                return "broadcastworld";
            case "chn":
                return "channel";
            case "chatlab":
                return "clab";
            case "cspy":
                return "commandspy";
            case "ac":
                return "helpop";
            case "pm":
            case "tell":
            case "t":
            case "w":
            case "whisper":
                return "msg";
            case "r":
                return "reply";
            case "sspy":
                return "socialspy";
            case "sc":
                return "staffchat";
        }
        return command;
    }

    public String colorizeOldHexColors(String message) {

        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            message = net.md_5.bungee.api.ChatColor.matcher.start() + 1, matcher.end()));

        }
    }
    public static Component convertTextToComponent(Player player, String path, String message) {


        String formattedMessage;
        if (!senderManager.hasPermission(player, "chat-format", "color")) {
            formattedMessage = MiniMessage.get().serialize(MiniMessage.get().parse(TextUtils.convertLegacyToMiniMessage(message)));
        } else {
            formattedMessage = TextUtils.convertLegacyToMiniMessage(message);
        }

        String formattedPath = path
                .replace("%message%", formattedMessage);

        formattedPath = PlaceholderUtils.replaceAllVariables(player, formattedPath);
        formattedPath = TextUtils.convertLegacyToMiniMessage(formattedPath);


        return MiniMessage.get().parse(formattedPath);
    }
    public static String convertText(Player player, String path, String message) {

        String formattedMessage;
        if (!senderManager.hasPermission(player, "chat-format", "color")){
            formattedMessage = "<pre>" + message + "</pre>";
        } else {
            formattedMessage = TextUtils.convertLegacyToMiniMessage(message);
        }

        String formattedPath  = path
                .replace("%message%", formattedMessage);

        formattedPath = PlaceholderUtils.replaceAllVariables(player, formattedPath);
        formattedPath = TextUtils.convertLegacyToMiniMessage(formattedPath);

        return formattedPath;

    }


    public static String convertLegacyToMiniMessage(String string) {

        if (!config.getBoolean("options.use-legacy-colors")) {
            return string;
        }
        String formattedPath = string
                .replace("&f", "<white>");

        return MiniMessage.get().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(formattedPath).asComponent());
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
