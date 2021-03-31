package code.managers.commands;

import code.PluginService;
import code.data.UserData;
import code.managers.MethodService;

import java.util.Map;
import java.util.UUID;

public class SocialSpyMethod implements MethodService {

    private final PluginService pluginService;

    private final Map<UUID, UserData> cache;

    private String status;

    public SocialSpyMethod(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache().getUserDatas();
    }

    public String getStatus() {
        return status;
    }

    public void toggleOption(UUID uuid) {
        UserData usercache = cache.get(uuid);

        if (usercache.isSocialSpyMode()) {
            usercache.toggleSocialSpy(false);
            status = pluginService.getFiles().getCommand().getString("commands.socialspy.player.variable-off");
            return;
        }

        usercache.toggleSocialSpy(true);
        status = pluginService.getFiles().getCommand().getString("commands.socialspy.player.variable-on");
    }

    public void enableOption(UUID uuid) {
        cache.get(uuid).toggleSocialSpy(true);
    }

    public void disableOption(UUID uuid) {
        cache.get(uuid).toggleSocialSpy(false);
    }

}
