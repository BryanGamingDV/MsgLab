package me.bryangaming.chatlab.common.managers.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Option;
import me.bryangaming.chatlab.common.data.UserData;

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
        UserData usercache = cache.get(uuid);

        if (usercache.isPlayerHelpOp()) {
            usercache.toggleHelpOp(false);
            status = pluginService.getFiles().getCommandFile().getString("commands.helpop.player.variable-off");
            return;
        }

        usercache.toggleHelpOp(true);
        status = pluginService.getFiles().getCommandFile().getString("commands.helpop.player.variable-on");
    }

    public void enableOption(UUID uuid) {
        cache.get(uuid).toggleHelpOp(true);
    }

    public void disableOption(UUID uuid) {
        cache.get(uuid).toggleHelpOp(false);
    }

}
