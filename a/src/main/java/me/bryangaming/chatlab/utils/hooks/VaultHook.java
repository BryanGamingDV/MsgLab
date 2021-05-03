package me.bryangaming.chatlab.utils.hooks;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Hook;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements Hook {


    private final PluginService pluginService;

    private Permission permission;
    private Chat chat;
    private Economy economy;

    public VaultHook(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }


    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("Addons - Vault not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - Vault not enabled! Disabling support..", LoggerTypeEnum.WARNING);
            return;
        }

        if (pluginService.getFiles().getConfigFile().getBoolean("options.hooks.vault")){
            return;
        }

        pluginService.getPlugin().getLogger().info("Addons - Loading Vault..");

        RegisteredServiceProvider<Permission> rspP = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Chat> rspC = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        RegisteredServiceProvider<Economy> rspE = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if (rspP != null) {
            permission = rspP.getProvider();
        }
        if (rspC != null) {
            chat = rspC.getProvider();
        }
        if (rspE != null) {
            economy = rspE.getProvider();
        }

        pluginService.getPlugin().getLogger().info("Addons - Vault enabled! Enabling support..");
        pluginService.getLogs().log("Addons - Vault enabled! Enabling support...");
    }

    public Permission getPermissions() {
        return permission;
    }

    public Chat getChat() {
        return chat;
    }

    public Economy getEconomy() {
        return economy;
    }


}
