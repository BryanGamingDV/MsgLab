package me.bryangaming.chatlab.utils.string;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class TextUtils {

    private PluginService pluginService;
    private static Configuration config;

    public TextUtils(PluginService pluginService) {
        this.pluginService = pluginService;
        config = pluginService.getFiles().getConfig();
    }

    public static String setColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
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
