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
    private final Configuration formatsFile;

    public GroupManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
    }

    public Set<String> getGroup() {
        return formatsFile.getConfigurationSection("chat-format.groups").getKeys(false);
    }

    public String getPlayerGroup(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getChannelType() == GroupEnum.CHANNEL) {
            return userData.getChannelGroup();
        }

        if (player.isOp() || player.hasPermission("*")) {
            if (formatsFile.getConfigurationSection("chat-format.op") == null) {
                return "default";
            }

            return "op";
        }

        if (formatsFile.getString("chat-format.group-access").equalsIgnoreCase("permission")) {
            for (String group : getGroup()) {
                if (player.hasPermission(formatsFile.getString("chat-format.groups." + group + ".permission"))) {
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

        Configuration formatsFile = pluginService.getFiles().getFormatsFile();
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
        if (formatsFile.getString("join-and-quit.group-access").equalsIgnoreCase("permission")) {
            for (String group : formatsFile.getConfigurationSection("join-and-quit.formats").getKeys(false)) {
                if (formatsFile.getString("join-and-quit.formats." + group + ".permission") == null) {
                    continue;
                }

                if (player.hasPermission(formatsFile.getString("join-and-quit.formats." + group + ".permission"))) {
                    return group;
                }
            }
            return "default";
        }

        for (String group : formatsFile.getConfigurationSection("join-and-quit.formats").getKeys(false)) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }

    public Set<String> getConfigSection(GroupEnum channelType, Player player, String playerRank) {
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return formatsFile.getConfigurationSection("channel." + playerRank + ".bases").getKeys(false);
        }
        if (channelType == GroupEnum.PARTY) {
            return formatsFile.getConfigurationSection("party-chat.bases").getKeys(false);
        }

        if (formatsFile.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format.enabled")) {
            return formatsFile.getConfigurationSection("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases").getKeys(false);
        }

        switch (playerRank) {
            case "default":
            case "op":
                return formatsFile.getConfigurationSection("chat-format." + playerRank + ".bases").getKeys(false);
            default:
                return formatsFile.getConfigurationSection("chat-format.groups." + playerRank + ".bases").getKeys(false);
        }
    }

    public String getPlayerFormat(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return formatsFile.getString("channel." + playerRank + ".bases." + format + ".format");
        }
        if (channelType == GroupEnum.PARTY) {
            return formatsFile.getString("party-chat.bases." + format + ".format");
        }

        if (formatsFile.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format.enabled")) {
            return formatsFile.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format. " + format + ". format");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return formatsFile.getString("chat-format." + playerRank + ".bases." + format + ".format");
            default:
                return formatsFile.getString("chat-format.groups." + playerRank + ".bases." + format + ".format");
        }
    }

    public List<String> getPlayerHover(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return formatsFile.getStringList("channel." + playerRank + ".bases." + format + ".hover");
        }
        if (channelType == GroupEnum.PARTY) {
            return formatsFile.getStringList("party-chat.bases." + format + ".hover");
        }


        if (formatsFile.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format.enabled")) {
            return formatsFile.getStringList("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".chat-format.hover");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return formatsFile.getStringList("chat-format." + playerRank + ".bases." + format + ".hover");
            default:
                return formatsFile.getStringList("chat-format.groups." + playerRank + ".bases." + format + ".hover");
        }
    }

    public String getPlayerActionType(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return formatsFile.getString("channel." + playerRank + ".bases." + format + ".action.type");
        }
        if (channelType == GroupEnum.PARTY) {
            return formatsFile.getString("party-chat.bases." + format + ".action.type");
        }

        if (formatsFile.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format.enabled")) {
            return formatsFile.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".chat-format.action.type");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return formatsFile.getString("chat-format." + playerRank + ".bases." + format + ".action.type");
            default:
                return formatsFile.getString("chat-format.groups." + playerRank + ".bases." + format + ".action.type");
        }

    }

    public String getPlayerActionFormat(GroupEnum channelType, Player player, String playerRank, String format) {
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        if (channelType == GroupEnum.CHANNEL) {
            return formatsFile.getString("channel." + playerRank + ".bases." + format + ".action.format");
        }
        if (channelType == GroupEnum.PARTY) {
            return formatsFile.getString("party-chat.bases." + format + ".action.format");
        }

        if (formatsFile.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format.enabled")) {
            return formatsFile.getString("per-world-chat.worlds." + WorldData.getWorldID(player) + ".bases." + format + ".chat-format.action.format");
        }

        switch (playerRank) {
            case "default":
            case "op":
                return formatsFile.getString("chat-format." + playerRank + ".bases." + format + ".action.format");
            default:
                return formatsFile.getString("chat-format.groups." + playerRank + ".bases." + format + ".action.format");
        }

    }


    public boolean hasGroupPermission(Player player, String group) {
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();
        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        switch (formatsFile.getString("channel." + group + ".condition.type").toLowerCase()){
            case "permission":
                return player.hasPermission(formatsFile.getString("channel." + group + ".condition.format"));
            case "group":
                return vaultHook.getChat().playerInGroup(player, formatsFile.getString("channel." + group + ".condition.format"));
        }
        return false;

    }

    public String getFitlerGroup(Player player) {

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
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

        for (String group : filtersFile.getConfigurationSection("commands.tab-module.filter.groups").getKeys(false)) {
            if (vaultHook.getChat().playerInGroup(player, group)) {
                return group;
            }
        }

        return "default";
    }
}
