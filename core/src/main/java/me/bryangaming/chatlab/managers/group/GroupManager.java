package me.bryangaming.chatlab.managers.group;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
import me.bryangaming.chatlab.utils.WorldData;
import me.bryangaming.chatlab.utils.hooks.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class GroupManager {

    private final PluginService pluginService;
    private final Configuration formatsFile;
    private final DebugLogger debugLogger;

    public GroupManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.formatsFile = pluginService.getFiles().getFormatsFile();
        this.debugLogger = pluginService.getDebugger();
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

        DebugLogger debugLogger = pluginService.getDebugger();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[Server] | Error: Vault isn't loaded..");
            debugLogger.log("[Server] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isHookEnabled("Vault")){
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

    public String getGroup(Player player, ConfigurationSection configurationSection){

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isHookEnabled("Vault")){
            pluginService.getPlugin().getLogger().info("[Server] | Error: The hook is disabled..");
            return "default";
        }

        VaultHook vaultHook = pluginService.getSupportManager().getVaultSupport();

        if (vaultHook.getChat() == null || vaultHook.getPermissions() == null) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault complement [LuckPerms, PermissionsEx..] isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        switch (configurationSection.getString("group-access")) {
            case "group":

                String group = configurationSection.getString("groups." + vaultHook.getPermissions().getPrimaryGroup(player));

                if (group == null){
                    return "default";
                }

                return group;
            case "permission":
                for (String groupPath : configurationSection.getConfigurationSection("groups").getKeys(false)){
                    if (!player.hasPermission(configurationSection.getString(groupPath + ".permission"))){
                        continue;
                    }

                    return groupPath;
                }
        }

        return "default";
    }

    public String getJQGroup(Player player) {

        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isHookEnabled("Vault")){
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


    public String getFormat(GroupEnum channelType, Player player, String playerRank){
        if (channelType == GroupEnum.CHANNEL) {
            return "channel." + playerRank;
        }
        if (channelType == GroupEnum.PARTY) {
            return "party-chat";
        }

        if (formatsFile.getBoolean("per-world-chat.worlds." + WorldData.getWorldID(player) + ".chat-format.enabled")) {
            return "per-world-chat.worlds." + WorldData.getWorldID(player);
        }

        switch (playerRank) {
            case "default":
            case "op":
                return "chat-format." + playerRank;
            default:
                return "chat-format.groups." + playerRank;
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
        DebugLogger debugLogger = pluginService.getDebugger();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            pluginService.getPlugin().getLogger().info("[SendTextListener] | Error: Vault isn't loaded..");
            debugLogger.log("[SendTextListener] | Vault isn't loaded..", LoggerTypeEnum.ERROR);
            return "default";
        }

        if (!TextUtils.isHookEnabled("Vault")){
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
