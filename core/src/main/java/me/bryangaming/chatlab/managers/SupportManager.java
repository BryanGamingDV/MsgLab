package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.hooks.*;

public class SupportManager {

    private final PluginService pluginService;

    private VaultHook vaultHook;
    private VanishHook vanishHook;
    private CMIVanishHook cmiVanishHook;
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
        cmiVanishHook = new CMIVanishHook(pluginService);
        vanishHook = new VanishHook(pluginService);
    }

    public CMIVanishHook getCmiVanishSupport() {
        return cmiVanishHook;
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
