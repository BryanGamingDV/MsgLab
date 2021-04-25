package me.bryangaming.chatlab.utils;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.hooks.DiscordHook;
import me.bryangaming.chatlab.utils.hooks.ProtocolHook;
import me.bryangaming.chatlab.utils.hooks.VaultHook;

public class SupportManager {

    private PluginService pluginService;

    private VaultHook vaultHook;
    private ProtocolHook protocolHook;
    private DiscordHook discordHook;

    public SupportManager(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        vaultHook = new VaultHook(pluginService);
        protocolHook = new ProtocolHook(pluginService);
        discordHook = new DiscordHook(pluginService);
    }

    public VaultHook getVaultSupport() {
        return vaultHook;
    }

    public ProtocolHook getProtocolSupport() {
        return protocolHook;
    }

    public DiscordHook getDiscordSupport() {
        return discordHook;
    }
}
