package code.methods.commands;

import code.Manager;
import code.cache.UserData;
import code.methods.MethodService;

import java.util.Map;
import java.util.UUID;

public class HelpOpMethod implements MethodService {

    private final Manager manager;
    private final Map<UUID, UserData> cache;

    private String status;

    public HelpOpMethod(Manager manager) {
        this.manager = manager;
        this.cache = manager.getCache().getPlayerUUID();
    }
    public String getStatus(){
        return status;
    }

    public void toggleOption(UUID uuid){
        UserData usercache = cache.get(uuid);

        if (usercache.isPlayerHelpOp()) {
            usercache.toggleHelpOp(false);
            status = manager.getFiles().getCommand().getString("commands.helpop.player.variable-off");
            return;
        }

        usercache.toggleHelpOp(true);
        status = manager.getFiles().getCommand().getString("commands.helpop.player.variable-on");
    }

    public void enableOption(UUID uuid){
        cache.get(uuid).toggleHelpOp(true);
    }

    public void disableOption(UUID uuid){
        cache.get(uuid).toggleHelpOp(false);
    }

}
