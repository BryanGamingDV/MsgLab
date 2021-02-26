package code.utils;

import code.PluginService;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import javax.xml.stream.util.EventReaderDelegate;


public class StringFormat {

    private final ConfigManager config;
    private final PluginService pluginService;

    private static SupportManager supportManager;

    public StringFormat(ConfigManager config, PluginService pluginService) {
        this.config = config;
        this.pluginService = pluginService;
        supportManager = pluginService.getSupportManager();
    }

    public void loopString(Player sender, Configuration config, String string) {
        PlayerMessage player = pluginService.getPlayerMethods().getSender();
        for (String msg : config.getStringList(string)) {
            player.sendMessage(sender, msg);
        }
    }

    public String replaceString(String string) {
        for (String keys : config.getConfig().getConfigurationSection("options.replacer").getKeys(false)) {
            string = string.replace(config.getConfig().getString("options.replacer." + keys + ".variable"),
                    config.getConfig().getString("options.replacer." + keys + ".format"));
        }

        return string
                .replace("%newline%", "\n");

    }

    public String getVersion(Server server){
        String version = server.getClass().getPackage().getName().split("\\.")[3];

        return version.replace("_",".").substring(1, version.length() - 3);
    }

    public boolean containsVersion(String version, String... versions){
        for (String versionPath : versions){
            if (version.equalsIgnoreCase(versionPath)){
                return true;
            }
        }
        return false;
    }

    public int getStringId(String string, char character, int id) {

        int index = 1;
        int number = 0;

        while (string.indexOf(character, number) != -1) {
            if (index == id) {
                break;
            }

            number = string.indexOf(character, number + 1);
            index++;
        }
        return number;
    }


    public int countRepeatedCharacters(String string, char character) {

        int counted = 0;
        int count = 0;

        while (string.indexOf(character, count) != -1) {

            counted = string.indexOf(character, counted + 1);
            count++;
        }

        return count;
    }


    public static String replaceVault(Player player, String string) {
        Permission permission = supportManager.getVaultSupport().getPermissions();
        Chat chat = supportManager.getVaultSupport().getChat();

        if (chat == null) {
            return string;
        }

        return string.replace("%prefix%", chat.getPlayerPrefix(player)
                .replace("%suffix%", chat.getPlayerSuffix(player))
                .replace("%group%", permission.getPrimaryGroup(player)));
    }

    public String replacePlayerVariables(Player player, String string) {
        return string
                // Player stats:
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%world%", player.getWorld().getName())

                // Level stats:
                .replace("%health%", String.valueOf(player.getHealth()))
                .replace("%maxhealth%", String.valueOf(player.getMaxHealth()))
                .replace("%foodlevel%", String.valueOf(player.getFoodLevel()))

                // Server stats:
                .replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()))
                .replace("%servername%", Bukkit.getServer().getName())
                .replace("%ip%", Bukkit.getIp());

    }
}
