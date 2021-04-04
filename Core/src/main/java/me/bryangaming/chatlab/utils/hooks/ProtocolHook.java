package me.bryangaming.chatlab.utils.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.bryangaming.chatlab.PluginService;
import org.bukkit.Bukkit;

public class ProtocolHook {

    private final PluginService pluginService;

    private ProtocolManager protocolSupport;

    public ProtocolHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            pluginService.getPlugin().getLogger().info("Addons - ProtocolLib not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - ProtocolLib not enabled! Disabling support..", 0);
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
