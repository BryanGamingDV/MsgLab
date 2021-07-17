package me.bryangaming.chatlab.utils.hooks.vanish;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.utils.text.TextUtils;
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
            pluginService.getDebugger().log("Addons - CMI not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (!pluginService.getFiles().getConfigFile().getBoolean("options.allow-hooks.cmi")){
            return;
        }
        vanishManager = CMI.getInstance().getVanishManager();
        pluginService.getPlugin().getLogger().info("Addons - Loaded CMI..");

    }

    public boolean isVanished(Player player){

        if (!Bukkit.getPluginManager().isPluginEnabled("CMI")){
            return false;
        }

        if (!TextUtils.isHookEnabled("CMI")) {
            return false;
        }

        return vanishManager.getAllVanished().contains(player.getUniqueId());
    }
}
