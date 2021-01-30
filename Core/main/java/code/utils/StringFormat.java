package code.utils;

import code.PluginService;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class StringFormat {

    private final ConfigManager config;
    private final PluginService pluginService;

    private static SupportManager supportManager;

    public StringFormat(ConfigManager config, PluginService pluginService){
        this.config = config;
        this.pluginService = pluginService;
        supportManager = pluginService.getSupportManager();
    }

    public void loopString(Player sender, Configuration config, String string){
        PlayerMessage player = pluginService.getPlayerMethods().getSender();
        for (String msg : config.getStringList(string)){
            player.sendMessage(sender, msg);
        }
    }

    public String replaceString(String string){
        return string
                .replace(config.getConfig().getString("config.p-variable"), config.getConfig().getString("config.prefix"))
                .replace(config.getConfig().getString("config.e-variable"), config.getConfig().getString("config.error"))
                .replace("%newline%", "\n");


    }
    public int getStringId(String string, char character, int id){

        int index = 1;
        int number = 0;

        while (string.indexOf(character, number) != -1) {
            if (index == id) {
                break;
            }
            System.out.println("String: " + string);
            System.out.println("Char: " + character);
            System.out.println("Number:" + number);
            number = string.indexOf(character, number + 1);
            index++;
        }
        System.out.println(number);
        return number;
    }

    public int countRepeatedCharacters(String string, char character){
        int count = 0;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }


    public static String replaceVault(Player player, String string){
        Permission permission = supportManager.getVaultSupport().getPermissions();
        Chat chat = supportManager.getVaultSupport().getChat();

        if (chat == null){
           return string;
        }

        return ChatColor.translateAlternateColorCodes('&', string.replace("%prefix%", chat.getPlayerPrefix(player)
                .replace("%suffix%", chat.getPlayerSuffix(player))
                .replace("%group%", permission.getPrimaryGroup(player))));
    }

    public String replacePlayerVariables(Player player, String string){
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
