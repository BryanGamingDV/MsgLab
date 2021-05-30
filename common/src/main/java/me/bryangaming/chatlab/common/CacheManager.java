package me.bryangaming.chatlab.common;

import me.bryangaming.chatlab.api.Listener;
import me.bryangaming.chatlab.common.builders.ListenerBuilder;
import me.bryangaming.chatlab.common.data.JQData;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.loader.EventLoader;
import me.bryangaming.chatlab.common.utils.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CacheManager {

    private final Map<UUID, List<String>> ignoreCache = new HashMap<>();
    private final Map<String, Configuration> listConfig = new HashMap<>();
    private final Map<String, Listener> listenerBuilderMap = new HashMap<>();

    private final Map<UUID, UserData> userData = new HashMap<>();
    private final Map<String, JQData> getJQFormat = new HashMap<>();

    public CacheManager(PluginService pluginService) {

        DebugLogger debug = pluginService.getLogs();
        debug.log("Configuration loaded!");
        debug.log("Playeruuid loaded!");

    }

    public Map<String, Listener> getListeners(){
        return listenerBuilderMap;
    }
    public Map<String, JQData> getJQFormats() {
        return getJQFormat;
    }

    public Map<UUID, UserData> getUserDatas() {
        return userData;
    }

    public Map<UUID, List<String>> getIgnorelist() {
        return ignoreCache;
    }

    public Map<String, Configuration> getConfigFiles() {
        return listConfig;
    }

}