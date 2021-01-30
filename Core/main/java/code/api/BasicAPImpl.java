package code.api;

import code.CacheManager;
import code.Manager;
import code.cache.UserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class BasicAPImpl implements BasicAPI {

    private final Manager manager;

    private static CacheManager cache;
    private static Map<UUID, UserData> userCacheMap;


    private static final String pluginname = "BasicMsg";
    private static final String pluginversion = "1.8.8";
    private static final String pluginauthor = "BryanGaming";


    public BasicAPImpl(Manager manager){
        this.manager = manager;

        cache = manager.getCache();
        userCacheMap = manager.getCache().getPlayerUUID();
    }

    public static boolean isPlayerIgnored(UUID targetPlayer, UUID playerIgnored){
        Player player = Bukkit.getPlayer(playerIgnored);

        if (player == null){
            return false;
        }

        Map<UUID, List<String>> ignorelist = cache.getIgnorelist();
        if (ignorelist.containsKey(targetPlayer)){
            return false;
        }
        List<String> ignoredplayers = ignorelist.get(targetPlayer);

        for(String ignoreplayers : ignoredplayers){
            if (ignoreplayers.contains(player.getName())){
                return true;
            }
        }
        return false;
    }


    public static List<String> getIgnoredPlayers(UUID uuid){
        if (!(cache.getIgnorelist().containsKey(uuid))){
            return null;
        }
        return cache.getIgnorelist().get(uuid);
    }

    public static List<String> getSpyList(){
        List<String> socialspyList = new ArrayList<>();

        for (UserData cache : userCacheMap.values()) {
            if (cache.isSocialSpyMode()) {
                socialspyList.add(cache.getPlayer().getName());
            }
        }
        return socialspyList;

    }

    public static List<String> getMsgToggleList(){
        List<String> msgToggle = new ArrayList<>();

        for (UserData cache : userCacheMap.values()) {
            if (cache.isMsgtoggleMode()) {
                msgToggle.add(cache.getPlayer().getName());
            }
        }

        return msgToggle;
    }

    public static UUID getRepliedPlayer(UUID uuid){
        return userCacheMap.get(uuid).getRepliedPlayer();
    }

    public static String getDescription(){
        return pluginname + "_" + pluginversion + "_" + pluginauthor;
    }

    public static String getPluginName(){
        return pluginname;
    }

    public static String getPluginVersion(){
        return pluginversion;
    }

    public static String getPluginAuthor(){
        return pluginauthor;
    }



}