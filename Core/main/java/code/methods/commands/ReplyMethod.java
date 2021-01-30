package code.methods.commands;

import code.CacheManager;
import code.Manager;
import code.cache.UserData;

import java.util.UUID;

public class ReplyMethod{

    private Manager manager;
    private CacheManager cache;

    public ReplyMethod(Manager manager) {
        this.manager = manager;
        this.cache = manager.getCache();
    }

    public void setReply(UUID player, UUID target){

        UserData playerCache = manager.getCache().getPlayerUUID().get(player);
        UserData targetCache = manager.getCache().getPlayerUUID().get(target);

        playerCache.setRepliedPlayer(target);
        targetCache.setRepliedPlayer(player);
    }
}
