package me.bryangaming.chatlab.common.listeners.command;


import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.SocialSpyEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SocialSpyListener implements Listener {

    private PluginService pluginService;

    public SocialSpyListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onSocialSpy(SocialSpyEvent socialSpyEvent) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            UserData watcherSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

            if (!watcherSpy.isSocialSpyMode()) {
                return;
            }

            senderManager.sendMessage(player, socialSpyEvent.getMessage());
            senderManager.playSound(player, SoundEnum.RECEIVE_SOCIALSPY);
        });
    }
}
