package code.methods;

import code.PluginService;
import code.bukkitutils.WorldData;
import code.debug.DebugLogger;
import code.utils.Configuration;
import code.utils.addons.VaultSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class GroupMethod {

    private final PluginService pluginService;

    public GroupMethod(PluginService pluginService){
        this.pluginService = pluginService;
    }

    public Set<String> getGroup(){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        return utils.getConfigurationSection("chat.format.groups").getKeys(false);
    }
    public String getPlayerGroup(Player player){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (player.isOp() || player.hasPermission("*")){
            if (utils.getConfigurationSection("chat.format.op") == null){
                return "default";
            }

            return "op";
        }

        if (utils.getString("chat.format.group-access", "gr").equalsIgnoreCase("permission")) {
            for (String group : getGroup()) {
                if (player.hasPermission(utils.getString("chat.format.groups." + group + ".permission"))) {
                    return group;
                }
            }
            return "default";
        }

        DebugLogger debugLogger = pluginService.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")){
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        VaultSupport vaultSupport = pluginService.getSupportManager().getVaultSupport();

        if (vaultSupport.getChat() == null || vaultSupport.getPermissions() == null){
            pluginService.getPlugin().getLogger().info("[ChatFormat] | Error: Vault complement [LuckPerms, PermissionsEx..] isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        for (String group : getGroup()) {
            if (vaultSupport.getChat().playerInGroup(player, group)){
                return "group";
            }
        }

        return "default";
    }

    public String getPlayerFormat(Player player){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("chat.per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")){
            return utils.getString("chat.per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.format");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default") ){
            return utils.getString("chat.format." + getPlayerGroup(player) + ".format");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("op") ){
            return utils.getString("chat.format." + getPlayerGroup(player) + ".format");
        }

        return utils.getString("chat.format.groups." + getPlayerGroup(player) + ".format");

    }

    public List<String> getPlayerHover(Player player){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("chat.per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")){
            return utils.getStringList("chat.per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.hover");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default") ){
            return utils.getStringList("chat.format." + getPlayerGroup(player) + ".hover");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op") ){
            return utils.getStringList("chat.format." + getPlayerGroup(player) + ".hover");
        }
        return utils.getStringList("chat.format.groups." + getPlayerGroup(player) + ".hover");

    }

    public String getPlayerCmd(Player player){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (utils.getBoolean("chat.per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.enabled")){
            return utils.getString("chat.per-world-chat.worlds." + WorldData.getWorldID(player) + ".format.command");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("default") ){
            return utils.getString("chat.format." + getPlayerGroup(player) + ".command");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op") ){
            return utils.getString("chat.format." + getPlayerGroup(player) + ".command");
        }

        return utils.getString("chat.groups.format." + getPlayerGroup(player) + ".command");

    }

    public boolean channelNotExists(String group){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        return utils.getString("chat.format.groups." + group) == null;
    }


    public boolean isChannelEnabled(String group){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (group.equalsIgnoreCase("default")){
            return true;
        }
        return utils.getBoolean("chat.format.groups." + group + ".channel");
    }

    public boolean hasGroupPermission(Player player, String group){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (group.equalsIgnoreCase("default")){
            return true;
        }

        return player.hasPermission(utils.getString("chat.format.groups." + group + ".permission"));

    }

}
