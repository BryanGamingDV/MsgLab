package me.bryangaming.chatlab.common.utils.hooks;

import de.myzelyam.api.vanish.VanishAPI;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.debug.LoggerTypeEnum;

import org.bukkit.OfflinePlayer;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

public class VanishHook implements Hook {

    private PluginService pluginService;

    public VanishHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PremiumVanish") || !Bukkit.getPluginManager().isPluginEnabled("SuperVanish")) {
            pluginService.getPlugin().getLogger().info("Addons - SuperVanish not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - SuperVanish not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (pluginService.getFiles().getConfigFile().getBoolean("options.hooks.protocollib")){
            return;
        }

        pluginService.getPlugin().getLogger().info("Addons - Loaded SuperVanish..");
    }

    public boolean isVanished(OfflinePlayer offlinePlayer){
        return isVanished(offlinePlayer.getPlayer());
    }

    public boolean isVanished(PlayerWrapper player){
        if (!Bukkit.getPluginManager().isPluginEnabled("PremiumVanish") || !Bukkit.getPluginManager().isPluginEnabled("SuperVanish")) {
            return false;
        }

        if (!TextUtils.isAllowedHooked("SuperVanish")) {
            return false;
        }

        return VanishAPI.isInvisible(player);
    }
}
