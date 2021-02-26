package code.utils.addons;

import code.PluginService;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ProtocolSupport {

    private final PluginService pluginService;

    private ProtocolManager protocolSupport;

    public ProtocolSupport(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            pluginService.getPlugin().getLogger().info("Addons - ProtocolLib not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - ProtocolLib not enabled! Disabling support..", 0);
            return;
        }

        protocolSupport = ProtocolLibrary.getProtocolManager();
        pluginService.getPlugin().getLogger().info("Addons - Loading ProtocolLib..");
        pluginService.getPlugin().getLogger().info("Addons - ProtocolLib enabled! Enabling support..");
        pluginService.getLogs().log("Addons - ProtocolLib enabled! Enabling support...");
    }

    public ProtocolManager getManager() {
        return protocolSupport;
    }
}
