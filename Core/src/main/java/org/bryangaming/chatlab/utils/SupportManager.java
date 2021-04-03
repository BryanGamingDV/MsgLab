package org.bryangaming.chatlab.utils;

import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.utils.hooks.ProtocolHook;
import org.bryangaming.chatlab.utils.hooks.VaultHook;

public class SupportManager {

    private PluginService pluginService;

    private VaultHook vaultHook;
    private ProtocolHook protocolHook;

    public SupportManager(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        vaultHook = new VaultHook(pluginService);
        protocolHook = new ProtocolHook(pluginService);
    }

    public VaultHook getVaultSupport() {
        return vaultHook;
    }

    public ProtocolHook getProtocolSupport() {
        return protocolHook;
    }
}
