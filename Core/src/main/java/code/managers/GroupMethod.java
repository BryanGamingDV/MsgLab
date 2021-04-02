package code.managers;

import code.PluginService;
import code.bukkitutils.WorldData;
import code.debug.DebugLogger;
import code.utils.Configuration;
import code.utils.hooks.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class GroupMethod {

    private final PluginService pluginService;

    public GroupMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public Set<String> getGroup() {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        return utils.getConfigurationSection("format.groups").getKeys(false);
    }

    public String getPlayerGroup(Player player) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (player.isOp() || player.hasPermission("*")) {
            if (utils.getConfigurationSection("format.op") == null) {
                return "default";
            }

            return "op";
        }

        if (utils.getString("format.group-access").equalsIgnoreCase("permission")) {
            for (String group : getGroup()) {
                if (player.hasPermission(utils.getString("format.groups." + group + ".permission"))) {
                    return group;
                }
            }
            return "default";
        }

        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault complement [LuckPerms, Group Manager..] isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        for (String group : getGroup()) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }


    public String getPlayerFormat(Player player, String format) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format. " + format + ". format");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default")) {
            return utils.getString("format." + getPlayerGroup(player) + ".bases." + format + ".format");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op")) {
            return utils.getString("format." + getPlayerGroup(player) + ".bases." + format + ".format");
        }

        return utils.getString("format.groups." + getPlayerGroup(player) + ".bases." + format + ".format");

    }

    public String getGroupPermission(String channel) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (channel.equalsIgnoreCase("default")) {
            return "default";
        }

        if (channel.equalsIgnoreCase("op")) {
            return "op";
        }

        return utils.getString("format.groups." + channel + ".permission");
    }

    public String getJQGroup(Player player) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault complement [LuckPerms, PermissionsEx..] isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        if (utils.getString("lobby.group-access").equalsIgnoreCase("permission")) {
            for (String group : utils.getConfigurationSection("lobby.formats").getKeys(true)) {
                if (utils.getString("lobby.formats." + group + ".permission") == null){
                    continue;
                }

                if (player.hasPermission(utils.getString("lobby.formats." + group + ".permission"))){
                    return group;
                }
            }
            return "default";
        }

        for (String group : utils.getConfigurationSection("lobby.formats").getKeys(true)) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }


    public List<String> getPlayerHover(Player player, String format) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getStringList("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".format.hover");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default")) {
            return utils.getStringList("format." + getPlayerGroup(player) + ".bases." + format + ".hover");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op")) {
            return utils.getStringList("format." + getPlayerGroup(player) + ".bases." + format + ".hover");
        }
        return utils.getStringList("format.groups." + getPlayerGroup(player) + ".bases." + format + ".hover");

    }

    public Set<String> getConfigSection(Player player) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getConfigurationSection("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases").getKeys(false);
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default")) {
            return utils.getConfigurationSection("format." + getPlayerGroup(player) + ".bases").getKeys(false);
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op")) {
            return utils.getConfigurationSection("format." + getPlayerGroup(player) + ".bases").getKeys(false);

        }
        return utils.getConfigurationSection("format.groups." + getPlayerGroup(player) + ".bases").getKeys(false);

    }

    public String getPlayerCmd(Player player, String format) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".format.command");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default")) {
            return utils.getString("format." + getPlayerGroup(player) + ".bases." + format + ".command");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op")) {
            return utils.getString("format." + getPlayerGroup(player) + ".bases." + format + ".command");
        }

        return utils.getString("format.groups" + getPlayerGroup(player) + ".bases." + format + ".command");

    }

    public boolean channelNotExists(String group) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (group.equalsIgnoreCase("default")) {
            return false;
        }

        return utils.getString("format.groups." + group) == null;
    }


    public boolean isChannelEnabled(String group) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (group.equalsIgnoreCase("default")) {
            return true;
        }

        return utils.getBoolean("format.groups." + group + ".channel");
    }

    public boolean hasGroupPermission(Player player, String group) {
        Configuration utils = pluginService.getFiles().getBasicUtils();
        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (group.equalsIgnoreCase("default")) {
            return true;
        }

        if (player.isOp() || player.hasPermission("*")) {
            return true;
        }

        if (utils.getString("format.group-access").equalsIgnoreCase("permission")) {
            return player.hasPermission(utils.getString("format.groups." + group + ".permission"));
        }

        return vaultHook.getChat().playerInGroup(player, group);

    }

    public String getFitlerGroup(Player player) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault complement [LuckPerms, Group Manager..] isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        for (String group : utils.getStringList("fitler-cmd.groups")) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }
}
