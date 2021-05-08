package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Option;
import me.bryangaming.chatlab.data.UserData;

import java.util.Map;
import java.util.UUID;

public class SocialSpyManager implements Option {

    private final PluginService pluginService;

    private final Map<UUID, UserData> cache;

    private String status;

    public SocialSpyManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache().getUserDatas();
    }

    public String getStatus() {
        return status;
    }

    public void toggleOption(UUID uuid) {
        UserData userData = cache.get(uuid);

        if (userData.isSocialSpyMode()) {
            userData.toggleSocialSpy(false);
            status = pluginService.getFiles().getCommandFile().getString("commands.socialspy.player.variable-off");
            return;
        }

        userData.toggleSocialSpy(true);
        status = pluginService.getFiles().getCommandFile().getString("commands.socialspy.player.variable-on");
    }

    public void enableOption(UUID uuid) {
        cache.get(uuid).toggleSocialSpy(true);
    }

    public void disableOption(UUID uuid) {
        cache.get(uuid).toggleSocialSpy(false);
    }

}
