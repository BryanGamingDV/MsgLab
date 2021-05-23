package me.bryangaming.chatlab.common.managers.sound;

import me.bryangaming.chatlab.common.CacheManager;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;

import javax.xml.stream.Location;
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
        String versionname = version.split("\\.")[3];

        if (versionname.startsWith("v1_8")) {
            debug.log("Using " + versionname + ", enabling sounds. Version:" + versionname);
            return;
        }

        if (versionname.startsWith("v1_9")) {
            debug.log("Using " + versionname + ", enabling sounds. Version:" + versionname);
            return;
        }

        debug.log("Using " + versionname + ", warning the default sounds are from 1.8, 1.9, disabling it", LoggerTypeEnum.WARNING);
        debug.log("Modify the sound, to avoid errors");

        sound.set("config.enabled-all", false);
        sound.save();

    }

    public void setSound(UUID target, String path) {

        PlayerWrapper player = ServerWrapper.getData().getPlayer(target);
        Location location = player.getLocation();

        if (!(sound.getBoolean("config.enabled-all"))) {
            return;
        }

        UserData playerMsgToggle = pluginService.getCache().getUserDatas().get(target);

        if (!(playerMsgToggle.isPlayersoundMode())) {
            return;
        }

        Sound soundType = getSound(path + ".sound");

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration messages = pluginService.getFiles().getMessagesFile();

        if (soundType == null) {
            senderManager.sendMessage(player.getPlayer(), messages.getString("error.sound.no-exists"));
            return;
        }

        if (sound.getBoolean(path + ".enabled", true)) {
            player.playSound(location, soundType, sound.getInt("sounds.vol"), sound.getInt(path + ".pitch"));
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