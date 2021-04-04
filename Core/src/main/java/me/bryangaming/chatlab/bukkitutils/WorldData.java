package me.bryangaming.chatlab.bukkitutils;

import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WorldData {

    private final PluginService pluginService;

    private static ChatLab plugin;
    private static DebugLogger debugLogger;

    private static Configuration utils;

    public WorldData(PluginService pluginService) {
        this.pluginService = pluginService;
        utils = pluginService.getFiles().getBasicUtils();

        plugin = pluginService.getPlugin();
        debugLogger = pluginService.getLogs();
    }

    public static List<String> getWorldChat(Player player) {

        Set<String> pwcKeys = utils.getConfigurationSection("per-world-chat.worlds").getKeys(false);

        if (pwcKeys.isEmpty()) {
            plugin.getLogger().info("Ummm if you don't want to use perworldchat per groups, I will activate all-worlds, for you.");
            plugin.getLogger().info("- EasterEgg #1");
            utils.set("per-world-chat.all-worlds", true);
            utils.reload();
            return getAllWorldChat();
        }

        for (String id : pwcKeys) {
            List<String> worldData = new ArrayList<>(utils.getStringList("per-world-chat.worlds." + id + ".worlds"));

            for (String world : worldData) {
                if (player.getWorld().getName().equalsIgnoreCase(world)) {
                    return worldData;
                }
            }
        }

        return getAllWorldChat();
    }

    public static String getWorldID(Player player) {

        if (!utils.getBoolean("per-world-chat.enabled")) {
            return null;
        }

        Set<String> pwcKeys = utils.getConfigurationSection("per-world-chat.worlds").getKeys(false);
        if (pwcKeys.isEmpty()) {
            plugin.getLogger().info("Ummm if you don't want to use perworldchat per groups, I will activate all-worlds, for you.");
            plugin.getLogger().info("- EasterEgg #1");
            utils.set("per-world-chat.all-worlds", true);
            utils.reload();
            return null;
        }

        for (String id : pwcKeys) {
            List<String> worldData = new ArrayList<>(utils.getStringList("per-world-chat.worlds." + id + ".worlds"));

            for (String world : worldData) {
                if (player.getWorld().getName().equalsIgnoreCase(world)) {
                    return id;
                }
            }
        }

        return null;
    }

    public static List<String> getAllWorldChat() {

        List<String> worldNames = new ArrayList<>();

        List<String> exceptfolders = Arrays.asList("plugins", "logs", "cache");


        for (File file : Bukkit.getServer().getWorldContainer().listFiles()) {

            if (!file.isDirectory()) {
                continue;
            }

            boolean status = false;
            for (String folders : exceptfolders) {
                if (folders.equalsIgnoreCase(file.getName())) {
                    status = true;
                    break;
                }
            }

            if (status) {
                continue;
            }

            worldNames.add(file.getName());
        }

        return worldNames;
    }
}
