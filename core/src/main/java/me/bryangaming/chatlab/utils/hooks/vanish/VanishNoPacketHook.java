package me.bryangaming.chatlab.utils.hooks.vanish;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

public class VanishNoPacketHook implements Hook {

    private final PluginService pluginService;
    private VanishManager vanishManager;

    public VanishNoPacketHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
            pluginService.getPlugin().getLogger().info("Addons - VanishNoPacket not enabled! Disabling support..");
            pluginService.getDebugger().log("Addons - VanishNoPacket not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (!pluginService.getFiles().getConfigFile().getBoolean("options.allow-hooks.vanish-no-packet")){
            return;
        }

        try {
            vanishManager = VanishNoPacket.getManager();
        }catch (VanishNotLoadedException exception) {
            return;
        }
        pluginService.getPlugin().getLogger().info("Addons - Loaded VanishNoPacket..");

    }

    public boolean isVanished(Player player){

        if (!Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")){
            return false;
        }

        if (!TextUtils.isHookEnabled("VanishNoPacket")) {
            return false;
        }

        return vanishManager.isVanished(player);
    }

}
