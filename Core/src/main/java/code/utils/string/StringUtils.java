package code.utils.string;

import code.PluginService;
import code.utils.Configuration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.text.TextComponent;
import org.bukkit.ChatColor;

public class StringUtils {

    private PluginService pluginService;
    private static Configuration config;

    public StringUtils(PluginService pluginService){
        this.pluginService = pluginService;
        config = pluginService.getFiles().getConfig();
    }

    public static String setColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String convertLegacyToMiniMessage(String string){

        if (!config.getBoolean("options.use-legacy-colors")){
            return string;
        }

        string = string
                .replace("&f", "<white>");

        return MiniMessage.get().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(string).asComponent());
    }
}
