package code.revisor;

import code.CacheManager;
import code.MsgLab;
import code.PluginService;
import code.data.UserData;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CooldownData {

    private final PluginService pluginService;

    private final MsgLab plugin;
    private final CacheManager cache;
    private final PlayerMessage playerMethod;

    private final Configuration config;
    private final Configuration utils;

    public CooldownData(PluginService pluginService) {
        this.pluginService = pluginService;

        this.plugin = pluginService.getPlugin();

        this.config = pluginService.getFiles().getConfig();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.cache = pluginService.getCache();
        this.playerMethod = pluginService.getPlayerMethods().getSender();
        pluginService.getListManager().getModules().add("cooldown");

        int seconds = utils.getInt("chat.format.cooldown.text.seconds");
        pluginService.getServerData().setServerCooldown(seconds);
    }

    public boolean isTextSpamming(UUID uuid) {


        if (!pluginService.getPathManager().isOptionEnabled("cooldown")) {
            return false;
        }


        if (!(utils.getBoolean("chat.format.cooldown.text.enabled"))) {
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        UserData playerCooldown = cache.getUserDatas().get(uuid);

        if (playerMethod.hasPermission(player, "cooldown.chat-bypass")) {
            return false;
        }

        if (playerCooldown.isCooldownMode()) {
            playerMethod.sendMessage(player, utils.getString("chat.format.cooldown.text.message")
                    .replace("%seconds%", pluginService.getServerData().getServerTextCooldown()));
            return true;
        }

        playerCooldown.setCooldownMode(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownMode(false);
            }
        }, 20L * pluginService.getServerData().getServerCooldown());

        return false;
    }

    public boolean isCmdSpamming(UUID uuid) {

        if (!pluginService.getPathManager().isOptionEnabled("cooldown")) {
            return false;
        }

        if (!(utils.getBoolean("chat.format.cooldown.cmd.enabled"))) {
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        UserData playerCooldown = cache.getUserDatas().get(uuid);

        if (playerMethod.hasPermission(player, "cooldown.cmd-bypass")) {
            return false;
        }

        if (playerCooldown.isCooldownCmdMode()) {
            playerMethod.sendMessage(player, utils.getString("chat.format.cooldown.cmd.message")
                    .replace("%seconds%", pluginService.getServerData().getServerTextCooldown()));
            return true;
        }

        playerCooldown.setCooldownCmdMode(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownCmdMode(false);
            }

        }, 20L * utils.getInt("chat.format.cooldown.cmd.seconds"));

        return false;
    }

}

