package me.bryangaming.chatlab.common.revisor;

import me.bryangaming.chatlab.common.CacheManager;
import me.bryangaming.chatlab.common.ChatLab;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.module.ModuleType;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.utils.Configuration;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginBaseWrapper;

import java.util.UUID;

public class CooldownData {

    private final PluginService pluginService;

    private final PluginBaseWrapper plugin;
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

        PlayerWrapper player = ServerWrapper.getData().getPlayer(uuid);
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

        ServerWrapper.getData().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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

        PlayerWrapper player = ServerWrapper.getData().getPlayer(uuid);
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

        ServerData.claswsscheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerCooldown.setCooldownCmdMode(false);
            }

        }, 20L * pluginService.getServerData().getServerCmdCooldown());

        return false;
    }

}

