package org.bryangaming.chatlab.listeners.events;


import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import org.bryangaming.chatlab.data.UserData;
import org.bryangaming.chatlab.events.SocialSpyEvent;
import org.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SocialSpyListener implements Listener {

    private PluginService pluginService;

    public SocialSpyListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onSocialSpy(SocialSpyEvent socialSpyEvent) {

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            UserData watcherSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

            if (!watcherSpy.isSocialSpyMode()) {
                return;
            }

            playersender.sendMessage(player, socialSpyEvent.getMessage());
            playersender.sendSound(player, SoundEnum.RECEIVE_SOCIALSPY);
        });
    }
}
