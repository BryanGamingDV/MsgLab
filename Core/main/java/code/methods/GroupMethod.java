package code.methods;

import code.Manager;
import code.debug.DebugLogger;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.addons.VaultSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class GroupMethod {

    private final Manager manager;

    public GroupMethod(Manager manager){
        this.manager = manager;
    }

    public Set<String> getGroup(){
        Configuration utils = manager.getFiles().getBasicUtils();

        return utils.getConfigurationSection("utils.chat.format.groups").getKeys(false);
    }
    public String getPlayerGroup(Player player){
        Configuration utils = manager.getFiles().getBasicUtils();

        if (player.isOp() || player.hasPermission("*")){
            if (utils.getConfigurationSection("utils.chat.format.op") == null){
                return "default";
            }

            return "op";
        }

        if (utils.getString("utils.chat.format.group-access").equalsIgnoreCase("permission")) {
            for (String group : getGroup()) {
                if (player.hasPermission(utils.getString("utils.chat.format.groups." + group + ".permission"))) {
                    return group;
                }
            }
            return "default";
        }

        DebugLogger debugLogger = manager.getLogs();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")){
            manager.getPlugin().getLogger().info("[ChatFormat] | Error: Vault isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);
            return "default";
        }

        VaultSupport vaultSupport = manager.getSupportManager().getVaultSupport();

        if (vaultSupport.getChat() == null || vaultSupport.getPermissions() == null){
            manager.getPlugin().getLogger().info("[ChatFormat] | Error: Vault complement [LuckPerms, PermissionsEx..] isn't loaded..");
            debugLogger.log("[ChatFormat] | Vault isn't loaded..", 2);;
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
        Configuration utils = manager.getFiles().getBasicUtils();

        if (getPlayerGroup(player).equalsIgnoreCase("default") ){
            return utils.getString("utils.chat.format." + getPlayerGroup(player) + ".format");
        }

        if (getPlayerGroup(player).equalsIgnoreCase("op") ){
            return utils.getString("utils.chat.format." + getPlayerGroup(player) + ".format");
        }

        return utils.getString("utils.chat.format.groups." + getPlayerGroup(player) + ".format");

    }

    public List<String> getPlayerHover(Player player){
        Configuration utils = manager.getFiles().getBasicUtils();

        if (getPlayerGroup(player).equalsIgnoreCase("default") ){
            return utils.getStringList("utils.chat.format." + getPlayerGroup(player) + ".hover");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op") ){
            return utils.getStringList("utils.chat.format." + getPlayerGroup(player) + ".hover");
        }
        return utils.getStringList("utils.chat.format.groups." + getPlayerGroup(player) + ".hover");

    }

    public String getPlayerCmd(Player player){
        Configuration utils = manager.getFiles().getBasicUtils();

        if (getPlayerGroup(player).equalsIgnoreCase("default") ){
            return utils.getString("utils.chat.format." + getPlayerGroup(player) + ".command");
        }
        if (getPlayerGroup(player).equalsIgnoreCase("op") ){
            return utils.getString("utils.chat.format." + getPlayerGroup(player) + ".command");
        }

        return utils.getString("utils.chat.groups.format." + getPlayerGroup(player) + ".command");

    }

    public boolean channelNotExists(String group){
        Configuration utils = manager.getFiles().getBasicUtils();

        return utils.getString("utils.chat.format.groups." + group) == null;
    }


    public boolean isChannelEnabled(String group){
        Configuration utils = manager.getFiles().getBasicUtils();

        if (group.equalsIgnoreCase("default")){
            return true;
        }
        return utils.getBoolean("utils.chat.format.groups." + group + ".channel");
    }

    public boolean hasGroupPermission(Player player, String group){
        Configuration utils = manager.getFiles().getBasicUtils();

        if (group.equalsIgnoreCase("default")){
            return true;
        }

        return player.hasPermission(utils.getString("utils.chat.format.groups." + group + ".permission"));

    }

}
