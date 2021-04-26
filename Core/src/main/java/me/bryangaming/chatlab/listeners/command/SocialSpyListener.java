package me.bryangaming.chatlab.listeners.command;


import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.SocialSpyEvent;
import me.bryangaming.chatlab.managers.SenderManager;
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

        SenderManager playersender = pluginService.getPlayerManager().getSender();

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
