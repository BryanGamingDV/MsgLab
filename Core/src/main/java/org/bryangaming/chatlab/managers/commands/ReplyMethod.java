package org.bryangaming.chatlab.managers.commands;

import org.bryangaming.chatlab.CacheManager;
import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.data.UserData;

import java.util.UUID;

public class ReplyMethod {

    private PluginService pluginService;
    private CacheManager cache;

    public ReplyMethod(PluginService pluginService) {
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
