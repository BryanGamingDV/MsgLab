package me.bryangaming.chatlab.revisor;

import me.bryangaming.chatlab.CacheManager;
import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CooldownData {

    private final PluginService pluginService;

    private final ChatLab plugin;
    private final CacheManager cache;
    private final SenderManager senderManager;

    private final Configuration config;
    private final Configuration utils;

    public CooldownData(PluginService pluginService) {
        this.pluginService = pluginService;

        this.plugin = pluginService.getPlugin();

        this.config = pluginService.getFiles().getConfigFile();
        this.utils = pluginService.getFiles().getFormatsFile();

        this.cache = pluginService.getCache();
        this.senderManager = pluginService.getPlayerManager().getSender();
        pluginService.getServerData().setServerTextCooldown(utils.getInt("filters.cooldown.text.seconds"));
        pluginService.getServerData().setServerCmdCooldown(utils.getInt("filters.cooldown.cmd.seconds"));
    }

    public boolean isTextSpamming(UUID uuid) {

        if (pluginService.getListManager().isEnabledOption(ModuleType.MODULE, "cooldown")) {
            return false;
        }


        if (!(utils.getBoolean("filters.cooldown.text.enabled"))) {
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        UserData playerCooldown = cache.getUserDatas().get(uuid);

        if (senderManager.hasPermission(player, "cooldown.chat-bypass")) {
            return false;
        }

        if (playerCooldown.isCooldownMode()) {
            senderManager.sendMessage(player, utils.getString("filters.cooldown.text.message")
                    .replace("%seconds%", pluginService.getServerData().getServerTextCooldownInString()));
            return true;
        }

        playerCooldown.setCooldownMode(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownMode(false);
            }
        }, 20L * pluginService.getServerData().getServerTextCooldown());

        return false;
    }

    public boolean isCmdSpamming(UUID uuid) {

        if (pluginService.getListManager().isEnabledOption(ModuleType.MODULE, "cooldown")) {
            return false;
        }

        if (!(utils.getBoolean("filters.cooldown.cmd.enabled"))) {
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);
        UserData playerCooldown = cache.getUserDatas().get(uuid);

        if (senderManager.hasPermission(player, "cooldown.cmd-bypass")) {
            return false;
        }

        if (playerCooldown.isCooldownCmdMode()) {
            senderManager.sendMessage(player, utils.getString("filters.cooldown.cmd.message")
                    .replace("%seconds%", pluginService.getServerData().getServerCmdCooldownInString()));
            return true;
        }

        playerCooldown.setCooldownCmdMode(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownCmdMode(false);
            }

        }, 20L * pluginService.getServerData().getServerCmdCooldown());

        return false;
    }

}

