package code.utils;

import code.PluginService;
import code.utils.addons.VaultSupport;

public class SupportManager {

    private PluginService pluginService;

    private VaultSupport vaultSupport;

    public SupportManager(PluginService pluginService){
        this.pluginService = pluginService;
        setup();
    }

    public void setup(){
        vaultSupport = new VaultSupport(pluginService);
    }

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }
}
