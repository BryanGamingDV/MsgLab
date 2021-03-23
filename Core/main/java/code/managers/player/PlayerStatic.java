package code.managers.player;

import code.MsgLab;
import code.PluginService;
import code.managers.commands.ChatMethod;
import code.utils.Configuration;
import code.utils.StringFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PlayerStatic {

    private static Pattern pattern;
    private static Pattern rgBPattern;

    private static ChatMethod chatMethod;
    private static PlayerMessage playerMethod;
    private static MsgLab msgLab;
    private static Configuration config;

    private PluginService pluginService;

    public PlayerStatic(PluginService pluginService) {
        this.pluginService = pluginService;

        msgLab = pluginService.getPlugin();
        config = pluginService.getFiles().getConfig();

        playerMethod = pluginService.getPlayerMethods().getSender();
        chatMethod = pluginService.getPlayerMethods().getChatMethod();

    }

    public static String setFormat(Player player, String path) {

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            path = PlayerStatic.setVariables(player, path);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            path = StringFormat.replaceVault(player, path);
        }

        path = chatMethod.replaceTagsVariables(player, path);

        path = setColor(path);

        return path
                .replace("%world%", player.getWorld().getName())
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
    }

    public static String setFormat(Player player, String path, String message) {

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            path = PlayerStatic.setVariables(player, path);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            path = StringFormat.replaceVault(player, path);
        }

        path = chatMethod.replaceTagsVariables(player, path);
        path = setColor(path);


        if (!playerMethod.hasPermission(player, "color.variable")) {
            message = "<pre>" + message + "</pre>";
        } else {
            message = setColor(message);
        }

        path = path
                .replace("%message%", message);

        return path
                .replace("%world%", player.getWorld().getName())
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
    }

    public static String setVariables(Player player, String path) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            path = PlaceholderAPI.setPlaceholders(player, path);
            path = path.replace('§', '&');

            return path;
        }
        return path;
    }

    public static String setColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component convertText(String text) {

        System.out.println("Text: " + text);
        return MiniMessage.get().parse(text);
    }

    public static String listToString(List<String> stringList) {
        return String.join("\n", stringList);
    }

    public static String setPluginVariables(String path) {

        StringFormat stringFormat = msgLab.getManager().getStringFormat();
        return stringFormat.replaceString(path);
    }

    public static String setAllVariables(Player player, String path) {

        path = setVariables(player, path);

        return setPluginVariables(path);
    }

    public static List<String> setPlaceholdersList(Player player, List<String> stringList) {
        stringList.replaceAll(text -> PlayerStatic.setVariables(player, text));

        return stringList;
    }

    public static List<String> setPlaceholdersList(Player player, List<String> stringList, Boolean serverreplaces) {
        if (serverreplaces) {
            stringList.replaceAll(text -> PlayerStatic.setVariables(player, text)
                    .replace("%playername%", player.getName()));
        } else {
            stringList.replaceAll(text -> PlayerStatic.setVariables(player, text));
        }
        return stringList;
    }


    public static List<String> setFormatList(Player player, List<String> stringList) {

        List<String> newColor = new ArrayList<>();

        for (String string : stringList) {
            newColor.add(setFormat(player, string));
        }

        return newColor;
    }

    public static List<String> setColorList(List<String> stringList) {

        List<String> newColor = new ArrayList<>();

        for (String string : stringList) {
            newColor.add(setColor(string));
        }

        return newColor;
    }
}
