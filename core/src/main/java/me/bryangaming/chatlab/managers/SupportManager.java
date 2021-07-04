package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.hooks.*;
import me.bryangaming.chatlab.utils.hooks.vanish.CMIVanishHook;
import me.bryangaming.chatlab.utils.hooks.vanish.SuperVanishHook;
import me.bryangaming.chatlab.utils.hooks.vanish.VanishNoPacketHook;
import org.bukkit.Bukkit;

public class SupportManager {

    private final PluginService pluginService;

    private VaultHook vaultHook;
    private SuperVanishHook superVanishHook;
    private CMIVanishHook cmiVanishHook;
    private ProtocolHook protocolHook;
    private DiscordHook discordHook;
    private VanishNoPacketHook vanishNoPacketHook;

    public SupportManager(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        vaultHook = new VaultHook(pluginService);
        protocolHook = new ProtocolHook(pluginService);
        discordHook = new DiscordHook(pluginService);
        cmiVanishHook = new CMIVanishHook(pluginService);
        superVanishHook = new SuperVanishHook(pluginService);

        if (Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
            vanishNoPacketHook = new VanishNoPacketHook(pluginService);
        }
    }

    public VanishNoPacketHook getVanishNoPacketHook() {
        return vanishNoPacketHook;
    }

    public CMIVanishHook getCmiVanishSupport() {
        return cmiVanishHook;
    }

    public SuperVanishHook getVanishSupport() {
        return superVanishHook;
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
