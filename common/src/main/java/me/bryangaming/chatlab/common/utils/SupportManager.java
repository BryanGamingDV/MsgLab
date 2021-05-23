package me.bryangaming.chatlab.common.utils;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.utils.hooks.DiscordHook;
import me.bryangaming.chatlab.common.utils.hooks.ProtocolHook;
import me.bryangaming.chatlab.common.utils.hooks.VanishHook;
import me.bryangaming.chatlab.common.utils.hooks.VaultHook;

public class SupportManager {

    private final PluginService pluginService;

    private VaultHook vaultHook;
    private VanishHook vanishHook;
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
        vanishHook = new VanishHook(pluginService);
    }

    public VanishHook getVanishSupport() {
        return vanishHook;
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
