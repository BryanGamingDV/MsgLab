package me.bryangaming.chatlab.common.utils.hooks;

import github.scarsz.discordsrv.DiscordSRV;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.common.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.common.listeners.DiscordSrvListener;


public class DiscordHook implements Hook {

    private final PluginService pluginService;

    public DiscordHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            pluginService.getPlugin().getLogger().info("Addons - DiscordSRV not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - DiscordSRV not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (pluginService.getFiles().getConfigFile().getBoolean("options.hooks.discordsrv")){
            return;
        }

        DiscordSRV.api.subscribe(new DiscordSrvListener(pluginService));
        pluginService.getPlugin().getLogger().info("Addons - Loaded DiscordSRV..");

    }
}
