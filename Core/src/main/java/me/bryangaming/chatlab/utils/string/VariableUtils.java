package me.bryangaming.chatlab.utils.string;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.commands.ChatMethod;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.SupportManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VariableUtils {

    private PluginService pluginService;

    private static SupportManager supportManager;
    private static ChatMethod chatMethod;

    private static Configuration config;
    private static Configuration utils;

    public VariableUtils(PluginService pluginService){
        this.pluginService = pluginService;

        supportManager = pluginService.getSupportManager();
        chatMethod = pluginService.getPlayerMethods().getChatMethod();

        config = pluginService.getFiles().getConfig();
        utils = pluginService.getFiles().getBasicUtils();
    }

    public static String replaceAllVariables(Player player, String string){

        string = replaceEmojis(string);
        string = replacePluginVariables(string);

        string = replaceTags(player, string);
        string = replacePlayerVariables(player, string);
        string = replacePAPIVariables(player, string);
        string = replaceVaultVariables(player, string);

        return string;
    }

    public static String replacePlayerVariables(Player player, String string){
        return string
                .replace("%world%", player.getWorld().getName())
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
    }

    public static String replacePAPIVariables(Player player, String string){
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            return string;
        }

        string = PlaceholderAPI.setPlaceholders(player, string);
        string = string.replace('§', '&');

        return string;
    }


    public static String replaceEmojis(String string){

        for (String emojiPath : utils.getStringList("filters.emojis")){
            string = string.replace(emojiPath.split(";")[0], emojiPath.split(";")[1]);
        }

        return string;
    }

    public static String replacePluginVariables(String string) {
        for (String keys : config.getConfigurationSection("options.replacer").getKeys(false)) {
            string = string.replace(config.getString("options.replacer." + keys + ".variable"),
                    config.getString("options.replacer." + keys + ".format"));
        }

        return string
                .replace("%newline%", "\n");

    }

    public static String replaceVaultVariables(Player player, String string){
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")){
            return string;
        }

        Permission permission = supportManager.getVaultSupport().getPermissions();
        Chat chat = supportManager.getVaultSupport().getChat();

        if (chat == null) {
            return string;
        }

        return string.replace("%prefix%", chat.getPlayerPrefix(player)
                .replace("%suffix%", chat.getPlayerSuffix(player))
                .replace("%group%", permission.getPrimaryGroup(player)));
    }

    public static String replaceTags(Player player, String string){
        return chatMethod.replaceTagsVariables(player, string);
    }
}
