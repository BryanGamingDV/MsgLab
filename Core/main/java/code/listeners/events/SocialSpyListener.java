package code.listeners.events;


import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.data.UserData;
import code.events.SocialSpyEvent;
import code.managers.player.PlayerMessage;
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
