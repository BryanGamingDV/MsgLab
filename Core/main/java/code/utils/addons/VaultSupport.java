package code.utils.addons;

import code.PluginService;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {


    private final PluginService pluginService;

    private Permission permission;
    private Chat chat;

    public VaultSupport(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }


    public void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("Addons - Vault not enabled! Disabling support..");
            pluginService.getLogs().log("Addons - Vault not enabled! Disabling support..", 0);
            return;
        }

        pluginService.getPlugin().getLogger().info("Addons - Loading Vault..");

        RegisteredServiceProvider<Permission> rpsP = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Chat> rspC = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);

        if (rspC == null) {
            pluginService.getPlugin().getLogger().info("Error - You need to have a Vault implementation plugin to use the provider.");
            pluginService.getPlugin().getLogger().info("Example - LuckPerms, UltraPermissions");
            pluginService.getPlugin().getLogger().info("Addons - Disabling support..");
            pluginService.getLogs().log("Error - Vault implementation not enabled! Disabling support..", 0);
            return;
        }


        permission = rpsP.getProvider();
        chat = rspC.getProvider();

        pluginService.getPlugin().getLogger().info("Addons - Vault enabled! Enabling support..");
        pluginService.getLogs().log("Addons - Vault enabled! Enabling support...");
    }

    public Permission getPermissions() {
        return permission;
    }

    public Chat getChat() {
        return chat;
    }
}
