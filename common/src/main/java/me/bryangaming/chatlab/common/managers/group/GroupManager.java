package me.bryangaming.chatlab.common.managers.group;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.utils.WorldData;
import me.bryangaming.chatlab.common.utils.hooks.VaultHook;
import me.bryangaming.chatlab.common.utils.string.TextUtils;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;

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

    public String getPlayerGroup(PlayerWrapper player) {
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

        if (!ServerWrapper.getData().isPluginEnabled( "Vault")) {
            pluginService.getPlugin().getLogger().info("[ServerWrapper] | Error: Vault isn't loaded..");
            debugLogger.log("[ServerWrapper] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isAllowedHooked("Vault")){
            pluginService.getPlugin().getLogger().info("[ServerWrapper] | Error: The hook is disabled..");
            return "default";
        }
        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[ServerWrapper] | Error: Vault complement [LuckPerms, Group Manager..] isn't loaded..");
            debugLogger.log("[ServerWrapper] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        for (String group : getGroup()) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }


    public String getJQGroup(PlayerWrapper player) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        DebugLogger debugLogger = pluginService.getLogs();

        if (!ServerWrapper.getData().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isAllowedHooked("Vault")){
            pluginService.getPlugin().getLogger().info("[ServerWrapper] | Error: The hook is disabled..");
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault complement [LuckPerms, PermissionsEx..] isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }
        if (utils.getString("lobby.group-access").equalsIgnoreCase("permission")) {
            for (String group : utils.getConfigurationSection("lobby.formats").getKeys(false)) {
                if (utils.getString("lobby.formats." + group + ".permission") == null) {
                    continue;
                }

                if (player.hasPermission(utils.getString("lobby.formats." + group + ".permission"))) {
                    return group;
                }
            }
            return "default";
        }

        for (String group : utils.getConfigurationSection("lobby.formats").getKeys(false)) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }

    public Set<String> getConfigSection(GroupEnum channelType, PlayerWrapper player, String playerRank) {
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

    public String getPlayerFormat(GroupEnum channelType, PlayerWrapper player, String playerRank, String format) {
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

    public List<String> getPlayerHover(GroupEnum channelType, PlayerWrapper player, String playerRank, String format) {
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

    public String getPlayerActionType(GroupEnum channelType, PlayerWrapper player, String playerRank, String format) {
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

    public String getPlayerActionFormat(GroupEnum channelType, PlayerWrapper player, String playerRank, String format) {
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


    public boolean hasGroupPermission(PlayerWrapper player, String group) {
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

    public String getFitlerGroup(PlayerWrapper player) {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isAllowedHooked("Vault")){
            pluginService.getPlugin().getLogger().info("[ServerWrapper] | Error: The hook is disabled..");
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
