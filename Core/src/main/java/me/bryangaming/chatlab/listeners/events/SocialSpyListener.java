package atogesputo.bryangaming.chatlab.listeners.events;


import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.data.UserData;
import atogesputo.bryangaming.chatlab.events.SocialSpyEvent;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import atogesputo.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
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
