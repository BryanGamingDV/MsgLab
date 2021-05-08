package me.bryangaming.chatlab.managers.group;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.WorldData;
import me.bryangaming.chatlab.utils.hooks.VaultHook;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class GroupManager {

    private final PluginService pluginService;

    public GroupManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public Set<String> getGroup() {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        return utils.getConfigurationSection("format.groups").getKeys(false);
    }

    public String getPlayerGroup(Player player) {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getChannelType() == GroupEnum.CHANNEL) {
            return userData.getChannelGroup();
        }

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
            pluginService.getPlugin().getLogger().info("[Server] | Error: Vault isn't loaded..");
            debugLogger.log("[Server] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isAllowedHooked("Vault")){
            pluginService.getPlugin().getLogger().info("[Server] | Error: The hook is disabled..");
            return "default";
        }
        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[Server] | Error: Vault complement [LuckPerms, Group Manager..] isn't loaded..");
            debugLogger.log("[Server] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        for (String group : getGroup()) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }


    public String getJQGroup(Player player) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isAllowedHooked("Vault")){
            pluginService.getPlugin().getLogger().info("[Server] | Error: The hook is disabled..");
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault complement [LuckPerms, PermissionsEx..] isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }
        if (utils.getString("lobby.group-access").equalsIgnoreCase("permission")) {
            for (String group : utils.getConfigurationSection("lobby.formats").getKeys(true)) {
                if (utils.getString("lobby.formats." + group + ".permission") == null) {
                    continue;
                }

                if (player.hasPermission(utils.getString("lobby.formats." + group + ".permission"))) {
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

    public Set<String> getConfigSection(GroupEnum channelType, Player player, String playerRank) {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return utils.getConfigurationSection("channel." + playerRank + ".bases").getKeys(false);
        }
        if (channelType == GroupEnum.PARTY) {
            return utils.getConfigurationSection("party-chat.bases").getKeys(false);
        }

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getConfigurationSection("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases").getKeys(false);
        }

        switch (playerRank) {
            case "default":
            case "op":
                return utils.getConfigurationSection("format." + playerRank + ".bases").getKeys(false);
            default:
                return utils.getConfigurationSection("format.groups." + playerRank + ".bases").getKeys(false);
        }
    }

    public String getPlayerFormat(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return utils.getString("channel." + playerRank + ".bases." + format + ".format");
        }
        if (channelType == GroupEnum.PARTY) {
            return utils.getString("party-chat.bases." + format + ".format");
        }

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format. " + format + ". format");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return utils.getString("format." + playerRank + ".bases." + format + ".format");
            default:
                return utils.getString("format.groups." + playerRank + ".bases." + format + ".format");
        }
    }

    public List<String> getPlayerHover(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return utils.getStringList("channel." + playerRank + ".bases." + format + ".hover");
        }
        if (channelType == GroupEnum.PARTY) {
            return utils.getStringList("party-chat.bases." + format + ".hover");
        }


        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getStringList("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".format.hover");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return utils.getStringList("format." + playerRank + ".bases." + format + ".hover");
            default:
                return utils.getStringList("format.groups." + playerRank + ".bases." + format + ".hover");
        }
    }

    public String getPlayerActionType(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return utils.getString("channel." + playerRank + ".bases." + format + ".action.type");
        }
        if (channelType == GroupEnum.PARTY) {
            return utils.getString("party-chat.bases." + format + ".action.type");
        }

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".format.action.type");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return utils.getString("format." + playerRank + ".bases." + format + ".action.type");
            default:
                return utils.getString("format.groups." + playerRank + ".bases." + format + ".action.type");
        }

    }

    public String getPlayerActionFormat(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration utils = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return utils.getString("channel." + playerRank + ".bases." + format + ".action.format");
        }
        if (channelType == GroupEnum.PARTY) {
            return utils.getString("party-chat.bases." + format + ".action.format");
        }

        if (utils.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")) {
            return utils.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".format.action.format");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return utils.getString("format." + playerRank + ".bases." + format + ".action.format");
            default:
                return utils.getString("format.groups." + playerRank + ".bases." + format + ".action.format");
        }

    }


    public boolean hasGroupPermission(Player player, String group) {
        Configuration utils = pluginService.getFiles().getFormatsFile();
        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        switch (utils.getString("channel." + group + ".condition.type").toLowerCase()){
            case "permission":
                return player.hasPermission(utils.getString("channel." + group + ".condition.format"));
            case "group":
                return vaultHook.getChat().playerInGroup(player, utils.getString("channel." + group + ".condition.format"));
        }
        return false;

    }

    public String getFitlerGroup(Player player) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isAllowedHooked("Vault")){
            pluginService.getPlugin().getLogger().info("[Server] | Error: The hook is disabled..");
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault complement [LuckPerms, Group Manager..] isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
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
