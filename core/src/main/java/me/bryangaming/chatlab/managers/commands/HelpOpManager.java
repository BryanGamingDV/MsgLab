package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Option;
import me.bryangaming.chatlab.data.UserData;

import java.util.Map;
import java.util.UUID;

public class HelpOpManager implements Option {

    private final PluginService pluginService;
    private final Map<UUID, UserData> cache;

    private String status;

    public HelpOpManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache().getUserDatas();
    }

    public String getStatus() {
        return status;
    }

    public void toggleOption(UUID uuid) {
        UserData userData = cache.get(uuid);

        if (userData.isPlayerHelpOp()) {
            userData.toggleHelpOp(false);
            status = pluginService.getFiles().getMessagesFile().getString("helpop.player.variable-off");
            return;
        }

        userData.toggleHelpOp(true);
        status = pluginService.getFiles().getMessagesFile().getString("helpop.player.variable-on");
    }

    public void enableOption(UUID uuid) {
        cache.get(uuid).toggleHelpOp(true);
    }

    public void disableOption(UUID uuid) {
        cache.get(uuid).toggleHelpOp(false);
    }

}
