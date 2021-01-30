package code;

import code.data.UserData;
import code.debug.DebugLogger;
import code.utils.Configuration;


import java.util.*;

public class CacheManager{
    private final Map<UUID, List<String>> ignoreCache = new HashMap<>();
    private final Map<String, Configuration> config = new HashMap<>();

    private final Map<UUID, UserData> playeruuid = new HashMap<>();

    private final PluginService pluginService;


    public CacheManager(PluginService pluginService){

        this.pluginService = pluginService;
        DebugLogger debug = pluginService.getLogs();
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
