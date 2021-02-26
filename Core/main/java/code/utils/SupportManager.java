package code.utils;

import code.PluginService;
import code.utils.addons.ProtocolSupport;
import code.utils.addons.VaultSupport;

public class SupportManager {

    private PluginService pluginService;

    private VaultSupport vaultSupport;
    private ProtocolSupport protocolSupport;

    public SupportManager(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        vaultSupport = new VaultSupport(pluginService);
        protocolSupport = new ProtocolSupport(pluginService);
    }

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }

    public ProtocolSupport getProtocolSupport() {
        return protocolSupport;
    }
}
