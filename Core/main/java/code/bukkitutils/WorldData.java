package code.bukkitutils;

import code.BasicMsg;
import code.PluginService;
import code.debug.DebugLogger;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class WorldManager{

    private final PluginService pluginService;

    private static BasicMsg plugin;
    private static DebugLogger debugLogger;

    private static Configuration utils;

    public WorldManager(PluginService pluginService){
        this.pluginService = pluginService;
        utils = pluginService.getFiles().getBasicUtils();

        plugin = pluginService.getPlugin();
        debugLogger = pluginService.getLogs();
    }

    public static List<String> getWorldChat(Player player){

        Set<String> pwcKeys = utils.getConfigurationSection("utils.chat.per-world-chat").getKeys(false);

        if (pwcKeys.isEmpty()){
            plugin.getLogger().info("Ummm if you don't want to use the perworldchat for groups, i will activate the all-worlds, for you.");
            plugin.getLogger().info("- EasterEgg #1");
            utils.set("utils.chat.per-world-chat.all-worlds", true);
            utils.reload();
            return getAllWorldChat();
        }

        for (String id : pwcKeys){
            List<String> worldData = new ArrayList<>(utils.getStringList("utils.chat.per-world-chat." + id));

            for (String world : worldData) {
                if (player.getWorld().getName().equalsIgnoreCase(world)){
                    return worldData;
                }
            }
        }

        return getAllWorldChat();
    }

    public static List<String> getAllWorldChat(){

        List<String> worldNames = new ArrayList<>();

        List<String> exceptfolders = Arrays.asList("plugins","logs", "cache");

        for (File file : Bukkit.getServer().getWorldContainer().listFiles()){
            if (file.isDirectory() && (!(exceptfolders.contains(file.getName())))){
                worldNames.add(file.getName());
            }
        }

        return worldNames;
    }
}
