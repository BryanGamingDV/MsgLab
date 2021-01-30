package code.utils.addons;

import code.Manager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.swing.plaf.synth.Region;
import java.security.Provider;

public class VaultSupport {


    private final Manager manager;

    private Permission permission;
    private Chat chat;

    public VaultSupport(Manager manager){
        this.manager = manager;
        setup();
    }


    public void setup(){
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")){
            manager.getPlugin().getLogger().info("Addons - Vault not enabled! Disabling support..");
            manager.getLogs().log("Addons - Vault not enabled! Disabling support..", 0);
            return;
        }

        manager.getPlugin().getLogger().info("Addons - Loading Vault..");

        RegisteredServiceProvider<Permission> rpsP = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Chat> rspC = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);

        if (rspC == null){
            manager.getPlugin().getLogger().info("Error - You need to have a vault implementation plugin to use the provider.");
            manager.getPlugin().getLogger().info("Example - LuckPerms, UltraPermissions");
            manager.getPlugin().getLogger().info("Addons - Disabling support..");
            manager.getLogs().log("Error - Vault implementation not enableOption! Disabling support..", 0);
            return;
        }


        permission = rpsP.getProvider();
        chat = rspC.getProvider();

        manager.getPlugin().getLogger().info("Addons - Vault enabled! Enabling support..");
        manager.getLogs().log("Addons - Vault enabled! Enabling support...");
    }

    public Permission getPermissions(){
        return permission;
    }

    public Chat getChat() {
        return chat;
    }
}
