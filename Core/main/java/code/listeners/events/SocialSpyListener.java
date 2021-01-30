package code.listeners.events;


import code.Manager;
import code.bukkitutils.SoundManager;
import code.cache.UserData;
import code.events.SocialSpyEvent;
import code.methods.player.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SocialSpyListener implements Listener{

    private Manager manager;

    public SocialSpyListener(Manager manager){
        this.manager = manager;
    }

    @EventHandler
    public void onSocialSpy(SocialSpyEvent socialSpyEvent){

        PlayerMessage playersender = manager.getPlayerMethods().getSender();
        SoundManager soundManager = manager.getManagingCenter().getSoundManager();

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            UserData watcherSpy = manager.getCache().getPlayerUUID().get(player.getUniqueId());

            if (!watcherSpy.isSocialSpyMode()) {
                return;
            }

            playersender.sendMessage(player, socialSpyEvent.getMessage());
            soundManager.setSound(player.getUniqueId(), "sounds.on-socialspy.receive-msg");
        });
    }
}
