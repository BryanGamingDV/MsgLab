package me.bryangaming.chatlab.managers.sound;

import me.bryangaming.chatlab.CacheManager;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import java.util.UUID;

public class SoundManager {


    private final PluginService pluginService;
    private final Configuration sound;

    private final CacheManager cache;
    private final DebugLogger debug;

    public SoundManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache();
        this.sound = pluginService.getFiles().getSoundsFile();
        this.debug = pluginService.getLogs();
        setup();
    }

    private void setup() {

        String version = Bukkit.getServer().getClass().getName();
        String versionName = version.split("\\.")[3].substring(0, version.split("\\.")[3].indexOf("_", 3));

        switch (versionName) {
            case "v1_8":
            case "v1_9":
                debug.log("Using " + versionName + ", enabling sounds. Version:" + versionName);
                break;
            default:
                debug.log("Using " + versionName + ", warning the default sounds are from 1.8, 1.9, disabling it", LoggerTypeEnum.WARNING);
                debug.log("Modify the sound, to avoid errors");

                sound.set("options.enabled-all", false);
                sound.save();
        }
    }

    public void setSound(UUID targetUniqueId, String path) {

        OfflinePlayer player = Bukkit.getPlayer(targetUniqueId);
        Location location = player.getPlayer().getLocation();

        if (!(sound.getBoolean("options.enabled-all"))) {
            return;
        }

        UserData playerMsgToggle = pluginService.getCache().getUserDatas().get(targetUniqueId);

        if (!(playerMsgToggle.isPlayersoundMode())) {
            return;
        }

        Sound soundType = getSound(path + ".sound");

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration messages = pluginService.getFiles().getMessagesFile();

        if (soundType == null) {
            senderManager.sendMessage(player.getPlayer(), messages.getString("global-errors.sound-not-available"));
            return;
        }

        if (sound.getBoolean(path + ".enabled", true)) {
            player.getPlayer().playSound(location, soundType, sound.getInt("sounds.vol"), sound.getInt(path + ".pitch"));
        }

    }

    private Sound getSound(String path) {
        try {
            return Sound.valueOf(sound.getString(path));

        } catch (IllegalArgumentException io) {
            pluginService.getLogs().log("Sound_Error" + io.getCause());

            for (StackTraceElement stack : io.getStackTrace()) {
                pluginService.getLogs().log(stack.toString());
            }

            return null;
        }
    }
}
