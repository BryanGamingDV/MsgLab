package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;

import java.util.UUID;

public class ReplyManager {

    private final PluginService pluginService;

    public ReplyManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setReply(UUID senderUniqueId, UUID targetUniqueId) {

        UserData senderData = pluginService.getCache().getUserDatas().get(senderUniqueId);
        UserData targetData = pluginService.getCache().getUserDatas().get(targetUniqueId);

        senderData.setRepliedPlayer(targetUniqueId);
        targetData.setRepliedPlayer(senderUniqueId);
    }

    public void setBungeeReply(UUID senderUniqueId, String target) {

        UserData targetData = pluginService.getCache().getUserDatas().get(senderUniqueId);
        targetData.setRepliedBungeePlayer(target);
    }
}
