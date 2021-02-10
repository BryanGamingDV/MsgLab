package code.bukkitutils;

import code.CacheManager;
import code.PluginService;
import code.data.UserData;
import code.debug.DebugLogger;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import java.util.UUID;

public class SoundCreator {


    private final PluginService pluginService;
    private final Configuration sound;

    private final CacheManager cache;
    private final DebugLogger debug;

    public SoundCreator(PluginService pluginService){
        this.pluginService = pluginService;
        this.cache = pluginService.getCache();
        this.sound = pluginService.getFiles().getSounds();
        this.debug = pluginService.getLogs();
        setup();
    }

    private void setup() {

        String version = Bukkit.getServer().getClass().getName();
        String versionname = version.split("\\.")[3];

        if (versionname.startsWith("v1_8")) {
            debug.log("Using " + versionname + ", enabling sounds. Version:" + versionname);
            return;
        }

        if (versionname.startsWith("v1_9")) {
            debug.log("Using "+ versionname +", enabling sounds. Version:" + versionname);
            return;
        }

        debug.log("Using "+ versionname +", warning the default sounds are from 1.8, 1.9, disabling it", 0);
        debug.log("Modify the sound, to avoid errors");

        sound.set("sounds.enabled-all", false);
        sound.save();

    }

    public void setSound(UUID target, String path){
        OfflinePlayer player = Bukkit.getPlayer(target);

        Location location = player.getPlayer().getLocation();

        if (!(sound.getBoolean("sounds.enabled-all"))){
            return;
        }

        UserData playerMsgToggle = pluginService.getCache().getPlayerUUID().get(target);
        if (!(playerMsgToggle.isPlayersoundMode())){
            return;
        }

        String subpath = path.split("\\.")[1];

        if (subpath.equalsIgnoreCase("on-socialspy")) {
            if (!(sound.getBoolean("sounds.on-socialspy.enabled-all"))) {
                return;
            }
        }

        if (subpath.equalsIgnoreCase("on-helpop")) {
            if (!(sound.getBoolean("sounds.on-helpop.enabled-all"))) {
                return;
            }
        }

        Sound soundType = getSound(path + ".sound");

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        Configuration messages = pluginService.getFiles().getMessages();

        if (soundType == null) {
            playersender.sendMessage(player.getPlayer(), messages.getString("error.sound.no-exists"));
            return;
        }

        if (sound.getBoolean(path + ".enabled")) {
            player.getPlayer().playSound(location, soundType, sound.getInt("sounds.vol"), sound.getInt(path + ".pitch"));
        }

    }
    private Sound getSound(String path){
        try {
            return Sound.valueOf(sound.getString(path));

        }catch( IllegalArgumentException io){
            pluginService.getLogs().log("Sound_Error" + io.getCause());

            for (StackTraceElement stack : io.getStackTrace()){
                pluginService.getLogs().log(stack.toString());
            }

            return null;
        }
    }
}
