package me.bryangaming.chatlab.modules;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.hooks.VaultHook;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckModule implements Module {

    private final PluginService pluginService;

    private final Logger logger;

    public CheckModule(PluginService pluginService) {
        this.pluginService = pluginService;
        this.logger = pluginService.getPlugin().getLogger();
    }

    @Override
    public void start() {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (utils.getBoolean("format.enabled")) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                printError("You need the Vault to use the SendTextListener please disable that option.");

            } else if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {

                printError("You need a Vault implementation to use the SendTextListener",
                        "Example: LuckPerms, UltraPermissions, etc...");
            }
        }

        if (utils.getBoolean("revisor-cmd.tab-module.block.enabled")) {
            if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                printError("You need the ProtocolLib to use the AntiTab, please disable that option.");
            }
        }

        if (utils.getBoolean("revisor-cmd.commands-module.conditions.enabled")) {
            if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                printError("You need the Vault to use the command conditions please disable that option.");

            } else if (vaultHook.getEconomy() == null) {
                printError("You need a Vault economy implementation to use the SendTextListener.");
            }
        }

    }

    private void printError(String... textList) {

        for (String text : textList) {
            logger.log(Level.WARNING, text);
        }
    }
}
