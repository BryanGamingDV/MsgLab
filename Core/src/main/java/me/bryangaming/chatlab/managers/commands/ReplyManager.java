package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;

import java.util.UUID;

public class ReplyManager {

    private final PluginService pluginService;

    public ReplyManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setReply(UUID player, UUID target) {

        UserData playerCache = pluginService.getCache().getUserDatas().get(player);
        UserData targetCache = pluginService.getCache().getUserDatas().get(target);

        playerCache.setRepliedPlayer(target);
        targetCache.setRepliedPlayer(player);
    }
}
