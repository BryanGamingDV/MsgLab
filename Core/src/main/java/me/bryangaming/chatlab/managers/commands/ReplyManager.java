package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.CacheManager;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;

import java.util.UUID;

public class ReplyManager {

    private PluginService pluginService;
    private CacheManager cache;

    public ReplyManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache();
    }

    public void setReply(UUID player, UUID target) {

        UserData playerCache = pluginService.getCache().getUserDatas().get(player);
        UserData targetCache = pluginService.getCache().getUserDatas().get(target);

        playerCache.setRepliedPlayer(target);
        targetCache.setRepliedPlayer(player);
    }
}
