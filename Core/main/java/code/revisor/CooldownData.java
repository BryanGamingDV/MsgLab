package code.revisor;

import code.BasicMsg;
import code.CacheManager;
import code.Manager;
import code.cache.UserData;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CooldownData {

    private Manager manager;

    private static BasicMsg plugin;
    private static CacheManager cache;
    private static PlayerMessage playersender;

    private static Configuration config;
    private static Configuration utils;

    public CooldownData(Manager manager){
        this.manager = manager;

        plugin = manager.getPlugin();

        config = manager.getFiles().getConfig();
        utils = manager.getFiles().getBasicUtils();

        cache = manager.getCache();
        playersender = manager.getPlayerMethods().getSender();
        manager.getListManager().getModules().add("cooldown");

    }

    public boolean isTextSpamming(UUID uuid){

        if (!manager.getPathManager().isOptionEnabled("cooldown")){
            return false;
        }

        if (!(utils.getBoolean("utils.chat.cooldown.text.enabled"))){
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        UserData playerCooldown = cache.getPlayerUUID().get(uuid);

        if (player.hasPermission(config.getString("config.perms.cooldown.chat-bypass"))){
            return false;
        }

        if (playerCooldown.isCooldownMode()) {
            playersender.sendMessage(player, utils.getString("utils.chat.cooldown.text.message")
                    .replace("%seconds%", utils.getString("utils.chat.cooldown.text.seconds")));
            return true;
        }

        playerCooldown.setCooldownMode(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownMode(false);
            }
        },20L * utils.getInt("utils.chat.cooldown.text.seconds"));

        return false;
    }

    public boolean isCmdSpamming(UUID uuid){

        if (!manager.getPathManager().isOptionEnabled("cooldown")){
            return false;
        }

        if (!(utils.getBoolean("utils.chat.cooldown.cmd.enabled"))){
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        UserData playerCooldown = cache.getPlayerUUID().get(uuid);

        if (player.hasPermission(config.getString("config.perms.cooldown.cmd-bypass"))){
            return false;
        }

        if (playerCooldown.isCooldownCmdMode()) {
            playersender.sendMessage(player, utils.getString("utils.chat.cooldown.cmd.message")
                    .replace("%seconds%", utils.getString("utils.chat.cooldown.cmd.seconds")));
            return true;
        }

        playerCooldown.setCooldownCmdMode(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownCmdMode(false);
            }
        },20L * utils.getInt("utils.chat.cooldown.cmd.seconds"));

        return false;
    }

}

