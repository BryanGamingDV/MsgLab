package code.managers.commands;

import code.CacheManager;
import code.PluginService;
import code.data.UserData;

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
