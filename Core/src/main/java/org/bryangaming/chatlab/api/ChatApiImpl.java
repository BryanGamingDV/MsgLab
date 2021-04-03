package org.bryangaming.chatlab.api;

import org.bryangaming.chatlab.CacheManager;
import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.data.UserData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatApiImpl implements BasicAPIDesc {

    private final PluginService pluginService;

    private CacheManager cache;
    private Map<UUID, UserData> userCacheMap;

    private final String pluginname = "ChatLab";
    private final String pluginversion = "2.0.1";
    private final String pluginauthor = "BryanGaming";


    public ChatApiImpl(PluginService pluginService) {
        this.pluginService = pluginService;

        cache = pluginService.getCache();
        userCacheMap = pluginService.getCache().getUserDatas();
    }

    public boolean isPlayerIgnored(UUID targetPlayer, UUID playerIgnored) {
        Player player = Bukkit.getPlayer(playerIgnored);

        if (player == null) {
            return false;
        }

        Map<UUID, List<String>> ignorelist = cache.getIgnorelist();
        if (ignorelist.containsKey(targetPlayer)) {
            return false;
        }
        List<String> ignoredplayers = ignorelist.get(targetPlayer);

        for (String ignoreplayers : ignoredplayers) {
            if (ignoreplayers.contains(player.getName())) {
                return true;
            }
        }
        return false;
    }


    public List<String> getIgnoredPlayers(UUID uuid) {
        if (!(cache.getIgnorelist().containsKey(uuid))) {
            return null;
        }
        return cache.getIgnorelist().get(uuid);
    }

    public List<String> getSpyList() {
        List<String> socialspyList = new ArrayList<>();

        for (UserData cache : userCacheMap.values()) {
            if (cache.isSocialSpyMode()) {
                socialspyList.add(cache.getPlayer().getName());
            }
        }
        return socialspyList;

    }

    public List<String> getMsgToggleList() {
        List<String> msgToggle = new ArrayList<>();

        for (UserData cache : userCacheMap.values()) {
            if (cache.isMsgtoggleMode()) {
                msgToggle.add(cache.getPlayer().getName());
            }
        }

        return msgToggle;
    }

    public UUID getRepliedPlayer(UUID uuid) {
        return userCacheMap.get(uuid).getRepliedPlayer();
    }

    public String getDescription() {
        return pluginname + "_" + pluginversion + "_" + pluginauthor;
    }

    public String getPluginName() {
        return pluginname;
    }

    public String getPluginVersion() {
        return pluginversion;
    }

    public String getPluginAuthor() {
        return pluginauthor;
    }


}
