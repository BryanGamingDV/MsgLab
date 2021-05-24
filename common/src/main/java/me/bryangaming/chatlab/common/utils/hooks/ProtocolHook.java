package me.bryangaming.chatlab.common.utils.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;


public class ProtocolHook implements Hook {

    private final PluginService pluginService;

    private ProtocolManager protocolSupport;

    public ProtocolHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!ServerWrapper.getData().isPluginEnabled("ProtocolLib")) {
            pluginService.getPlugin().getLogger().info("Addons - ProtocolLib not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - ProtocolLib not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (pluginService.getFiles().getConfigFile().getBoolean("options.hooks.protocollib")){
            return;
        }

        pluginService.getPlugin().getLogger().info("Addons - Loading ProtocolLib..");
        protocolSupport = ProtocolLibrary.getProtocolManager();

        pluginService.getPlugin().getLogger().info("Addons - ProtocolLib enabled! Enabling support..");
        pluginService.getLogs().log("Addons - ProtocolLib enabled! Enabling support...");
    }

    public ProtocolManager getManager() {
        return protocolSupport;
    }
}
