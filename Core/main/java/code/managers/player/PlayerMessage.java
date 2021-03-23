package code.managers.player;

import code.PluginService;
import code.bukkitutils.RunnableManager;
import code.bukkitutils.sound.SoundEnum;
import code.bukkitutils.sound.SoundManager;
import code.utils.Configuration;
import code.utils.StringFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerMessage {

    private final PluginService pluginService;

    private final Configuration config;
    private final Configuration sounds;

    public PlayerMessage(PluginService pluginService) {
        this.pluginService = pluginService;
        this.config = pluginService.getFiles().getConfig();
        this.sounds = pluginService.getFiles().getSounds();
    }


    public boolean hasPermission(Player player, String path) {

        Logger logger = pluginService.getPlugin().getLogger();
        String permission = config.getString("perms." + path);

        if (permission == null) {

            if (config.getString("perms." + StringUtils.remove(path, ".main")) == null) {

                if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                    logger.info("Please change the configuration section! Your config is old.");

                } else {
                    logger.info("Error - Could not find the path in config.");
                    logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

                }
            } else {
                permission = config.getString("perms." + StringUtils.remove(path, ".main"));
            }

        } else {
            if (permission.equalsIgnoreCase("none")) {
                return true;
            }
        }


        try {
            return player.hasPermission(permission);

        } catch (NullPointerException nullPointerException) {
            sendLines(nullPointerException);

        }
        return false;
    }


    public void sendMessage(Player sender, String path) {

        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                path = PlayerStatic.setVariables(sender, path);
            } catch (NullPointerException nullPointerException) {
                sendLines(nullPointerException);
                return;
            }
        }

        try {

            Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);


            path = pluginService.getStringFormat().replaceString(path);
            path = PlayerStatic.setFormat(sender, path);


            player.sendMessage(getMessage(path));
        } catch (NullPointerException nullPointerException) {
            sendLines(nullPointerException);
        }
    }

    public void sendMessage(Player sender, String path, String message) {
        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
        }

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        path = PlayerStatic.setFormat(sender, path, message);

        try {
            player.sendMessage(getMessage(path, message));
        } catch (NullPointerException nullPointerException) {
            sendLines(nullPointerException);
        }
    }

    public void sendMessage(Player sender, List<String> messages) {
        Logger logger = pluginService.getPlugin().getLogger();
        if (messages == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
        }

        StringFormat stringFormat = pluginService.getStringFormat();
        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        try {
            messages.replaceAll(message -> message = stringFormat.replaceString(message));
            messages.replaceAll(message ->
                    message = PlayerStatic.setFormat(sender, message));

        } catch (NullPointerException nullPointerException) {
            sendLines(nullPointerException);
            return;
        }

        for (String message : messages){
            player.sendMessage(getMessage(message));
        }
    }


    public void sendSound(Player player, SoundEnum soundType) {

        String path = soundType.getName();

        Configuration soundsFile = pluginService.getFiles().getSounds();
        SoundManager soundManager = pluginService.getManagingCenter().getSoundManager();

        Logger logger = pluginService.getPlugin().getLogger();
        String soundPath = soundsFile.getString("sounds." + path);

        if (soundPath == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");

            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
        }

        try {
            soundManager.setSound(player.getUniqueId(), "sounds." + path);

        } catch (NullPointerException nullPointerException) {
            sendLines(nullPointerException);
        }
    }

    public void sendSound(Player player, SoundEnum soundType, String command) {

        SoundManager sound = pluginService.getManagingCenter().getSoundManager();

        if (soundType != SoundEnum.ARGUMENT) {
            return;
        }

        if (!sounds.getBoolean("sounds.argument")) {
            return;
        }

        for (String keys : sounds.getConfigurationSection("group").getKeys(false)) {
            for (String newCommand : sounds.getStringList("group." + keys + ".commands")) {
                if (command.equalsIgnoreCase(newCommand.trim().replace(",", " "))) {
                    sound.setSound(player.getUniqueId(), "group." + keys);
                    return;
                }
            }
        }

        if (sounds.getConfigurationSection("argument." + command) == null) {
            return;
        }

        sound.setSound(player.getUniqueId(), "argument." + command);
    }

    public void sendCommand(Player player, String command) {

        RunnableManager runnableManager = pluginService.getManagingCenter().getRunnableManager();

        Logger logger = pluginService.getPlugin().getLogger();

        if (command == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");

            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
        }

        try {
            runnableManager.sendCommand(player, command);

        } catch (NullPointerException nullPointerException) {
            sendLines(nullPointerException);
            return;
        }

        runnableManager.sendCommand(player, command);
    }

    private Component getMessage(String message) {
        return PlayerStatic.convertText(message);
    }


    private Component getMessage(String path, String message) {
        path = pluginService.getStringFormat().replaceString(path);
        path = PlayerStatic.setColor(path);
        ;

        return PlayerStatic.convertText(path
                .replace("%message%", message));
    }

    private void sendLines(NullPointerException nullPointerException) {
        Logger logger = pluginService.getPlugin().getLogger();

        logger.info("Code line:");
        for (StackTraceElement stackTraceElement : nullPointerException.getStackTrace()) {

            String errorLine = stackTraceElement.toString();

            if (errorLine.contains("java.")) {
                break;
            }

            if (!errorLine.contains("code.")) {
                continue;
            }

            logger.info("- " + stackTraceElement.toString());

        }
    }
}
