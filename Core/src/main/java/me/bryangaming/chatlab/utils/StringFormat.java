package me.bryangaming.chatlab.utils;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.loader.FileLoader;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class StringFormat {

    private final FileLoader config;
    private final PluginService pluginService;

    private static SupportManager supportManager;

    public StringFormat( PluginService pluginService) {
        this.config = pluginService.getFiles();
        this.pluginService = pluginService;
        supportManager = pluginService.getSupportManager();
    }


    public String getVersion(Server server) {
        String version = server.getClass().getPackage().getName().split("\\.")[3];
        return version.replace("_", ".").substring(1, version.length() - 3);
    }

    public boolean containsVersion(String version, String... versions) {
        for (String versionPath : versions) {
            if (version.equalsIgnoreCase(versionPath)) {
                return true;
            }
        }
        return false;
    }

    public String[] getLegacyVersion() {

        return new String[]{"1.13", "1.14", "1.15", "1.16"};
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
