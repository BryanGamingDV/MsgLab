package code.methods.commands;

import code.Manager;
import code.cache.UserData;
import code.methods.MethodService;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

public class SocialSpyMethod implements MethodService {

    private final Manager manager;

    private final Map<UUID, UserData> cache;

    private String status;

    public SocialSpyMethod(Manager manager) {
        this.manager = manager;
        this.cache = manager.getCache().getPlayerUUID();
    }
    public String getStatus(){
        return status;
    }

    public void toggleOption(UUID uuid){
        UserData usercache = cache.get(uuid);

        if (usercache.isSocialSpyMode()) {
            usercache.toggleSocialSpy(false);
            status = manager.getFiles().getCommand().getString("commands.socialspy.player.variable-off");
            return;
        }

        usercache.toggleSocialSpy(true);
        status = manager.getFiles().getCommand().getString("commands.socialspy.player.variable-on");
    }

    public void enableOption(UUID uuid){
        cache.get(uuid).toggleSocialSpy(true);
    }

    public void disableOption(UUID uuid){
        cache.get(uuid).toggleSocialSpy(false);
    }

}
