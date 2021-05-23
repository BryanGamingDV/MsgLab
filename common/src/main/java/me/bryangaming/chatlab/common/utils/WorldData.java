package me.bryangaming.chatlab.common.utils;

import me.bryangaming.chatlab.common.ChatLab;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginBaseWrapper;

import java.beans.XMLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldData {

    private final PluginService pluginService;

    private static PluginBaseWrapper plugin;
    private static DebugLogger debugLogger;

    private static Configuration utils;

    public WorldData(PluginService pluginService) {
        this.pluginService = pluginService;
        utils = pluginService.getFiles().getFormatsFile();

        plugin = pluginService.getPlugin();
        debugLogger = pluginService.getLogs();
    }

    public static List<String> getWorldChat(PlayerWrapper player) {

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
                if (player.getWorldName().equalsIgnoreCase(world)) {
                    return worldData;
                }
            }
        }

        return null;
    }

    public static String getWorldID(PlayerWrapper player) {

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
            List<String> worldData = utils.getStringList("per-world-chat.worlds." + id + ".worlds");

            for (String world : worldData) {
                if (player.getWorldName().equalsIgnoreCase(world)) {
                    return id;
                }
            }
        }

        return null;
    }
}
