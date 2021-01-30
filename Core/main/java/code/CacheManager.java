package code;

import code.cache.UserData;
import code.debug.DebugLogger;
import code.utils.Configuration;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.ChatHexColor;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;


import java.util.*;

public class CacheManager{
    private final Map<UUID, List<String>> ignoreCache = new HashMap<>();
    private final Map<String, Configuration> config = new HashMap<>();

    private final Map<UUID, UserData> playeruuid = new HashMap<>();

    private final Manager manager;


    public CacheManager(Manager manager){

        this.manager = manager;
        DebugLogger debug = manager.getLogs();
        debug.log("Configuration loaded!");
        debug.log("Playeruuid loaded!");

    }
    public Map<UUID, UserData> getPlayerUUID() {
        return playeruuid;
    }

    public Map<UUID, List<String>> getIgnorelist(){
        return ignoreCache;
    }

    public Map<String, Configuration> getConfigFiles(){
        return config;
    }

}
