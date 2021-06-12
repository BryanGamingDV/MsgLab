package me.bryangaming.chatlab.utils.hooks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CMIVanishHook implements Hook {

    private final PluginService pluginService;
    private VanishManager vanishManager;

    public CMIVanishHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("CMI")) {
            pluginService.getPlugin().getLogger().info("Addons - CMI not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - CMI not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (pluginService.getFiles().getConfigFile().getBoolean("options.hooks.cmi")){
            return;
        }
        vanishManager = CMI.getInstance().getVanishManager();
        pluginService.getPlugin().getLogger().info("Addons - Loaded CMI..");

    }

    public boolean isVanished(Player player){

        if (!Bukkit.getPluginManager().isPluginEnabled("PremiumVanish") || !Bukkit.getPluginManager().isPluginEnabled("SuperVanish")) {
            return false;
        }

        if (!TextUtils.isAllowedHooked("CMI")) {
            return false;
        }

        return vanishManager.getAllVanished().contains(player.getUniqueId());
    }
}
