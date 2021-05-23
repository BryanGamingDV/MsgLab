package me.bryangaming.chatlab.common.managers;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.sound.SoundManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.SenderWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.List;
import java.util.logging.Logger;

public class SenderManager {

    private final PluginService pluginService;

    private final Configuration configFile;
    private final Configuration soundsFile;

    public SenderManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.configFile = pluginService.getFiles().getConfigFile();
        this.soundsFile = pluginService.getFiles().getSoundsFile();
    }


    public boolean hasUtilsPermission(PlayerWrapper player, String path) {

        Logger logger = pluginService.getPlugin().getLogger();

        if (path == null) {

            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
            TextUtils.printStackTrace();
            return false;
        }

        if (path.equalsIgnoreCase("none")) {
            return true;
        }

        return player.hasPermission(path);
    }

    public boolean hasPermission(SenderWrapper sender, String path) {
        if (!(sender instanceof PlayerWrapper)) {
            return true;
        }

        return hasPermission((PlayerWrapper) sender, path);
    }

    public boolean hasPermission(PlayerWrapper player, String path) {

        Logger logger = pluginService.getPlugin().getLogger();
        String permission = configFile.getString("perms." + path);

        if (permission == null) {

            if (configFile.getString("perms." + StringUtils.remove(path, ".main")) == null) {

                if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                    logger.info("Please change the configuration section! Your config is old.");

                } else {
                    logger.info("Error - Could not find the path in config.");
                    logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

                }

                TextUtils.printStackTrace();
                return false;
            }

            return player.hasPermission(configFile.getString("perms." + StringUtils.remove(path, ".main")));
        }

        if (permission.equalsIgnoreCase("none")) {
                return true;
        }

        return player.hasPermission(permission);

    }


    public void sendMessage(SenderWrapper sender, String path) {

        Logger logger = pluginService.getPlugin().getLogger();

        if (path == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }

            TextUtils.printStackTrace();
            return;
        }

        if (!(sender instanceof PlayerWrapper)) {
            System.out.println(path.replace("%newline%", "\n"));
            return;
        }

        Audience player = pluginService.getPlugin().getBukkitAudiences().player((PlayerWrapper) sender);
        player.sendMessage(TextUtils.convertTextToComponent((PlayerWrapper) sender, path));

    }

    public void sendMessage(OfflinePlayer sender, String path){
        sendMessage((PlayerWrapper) sender, path);
    }

    public void sendMessage(PlayerWrapper sender, String path) {

        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }

            TextUtils.printStackTrace();
            return;
        }


        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        player.sendMessage(TextUtils.convertTextToComponent(sender, path));
    }

    public void sendMessageTo(List<PlayerWrapper> playerList, String path) {

        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
            TextUtils.printStackTrace();
            return;
        }

        for (PlayerWrapper online : playerList) {
            Audience player = pluginService.getPlugin().getBukkitAudiences().player(online);
            player.sendMessage(TextUtils.convertTextToComponent(online, path));
        }
    }

    public void sendMessage(PlayerWrapper sender, String path, String message) {
        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {

            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
            TextUtils.printStackTrace();
            return;
        }

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        player.sendMessage(TextUtils.convertTextToComponent(sender, path, message));
    }

    public void sendMessage(PlayerWrapper sender, List<String> messages) {
        Logger logger = pluginService.getPlugin().getLogger();
        if (messages == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
            TextUtils.printStackTrace();
            return;
        }

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        messages.replaceAll(message -> TextUtils.convertText(sender, message));

        for (String message : messages) {

            player.sendMessage(MiniMessage.get().parse(message));
        }
    }


    public void playSound(SenderWrapper sender, SoundEnum soundType) {
        if (!(sender instanceof PlayerWrapper)){
            return;
        }

        playSound((PlayerWrapper) sender, soundType);
    }

    public void playSound(PlayerWrapper player, SoundEnum soundType) {

        String path = soundType.getName();

        Configuration soundsFile = pluginService.getFiles().getSoundsFile();
        SoundManager soundManager = pluginService.getPlayerManager().getSoundManager();

        Logger logger = pluginService.getPlugin().getLogger();
        String soundPath = soundsFile.getString("sounds." + path);

        if (soundPath == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");

            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
            TextUtils.printStackTrace();
            return;
        }

        soundManager.setSound(player.getUniqueId(), "sounds." + soundType.getName());
    }


    public SenderManager playSound(SenderWrapper sender, SoundEnum soundType, String command) {

        if (!(sender instanceof PlayerWrapper)){
            return this;
        }

        playSound((PlayerWrapper) sender, soundType, command);
        return this;
    }
    public SenderManager playSound(PlayerWrapper player, SoundEnum soundType, String command) {

        SoundManager sound = pluginService.getPlayerManager().getSoundManager();

        if (soundType != SoundEnum.ARGUMENT) {
            return this;
        }

        if (!soundsFile.getBoolean("sounds.argument")) {
            return this;
        }

        for (String keys : soundsFile.getConfigurationSection("group").getKeys(false)) {
            for (String newCommand : soundsFile.getStringList("group." + keys + ".commands")) {
                if (command.equalsIgnoreCase(newCommand.trim().replace(",", " "))) {
                    sound.setSound(player.getUniqueId(), "group." + keys);
                    return this;
                }
            }
        }

        if (soundsFile.getConfigurationSection("argument." + command) == null) {
            return this;
        }

        sound.setSound(player.getUniqueId(), "argument." + command);
        return this;
    }

    public void sendMessageLater(PlayerWrapper sender, int time, String command) {

        RunnableManager runnableManager = pluginService.getPlayerManager().getRunnableManager();

        Logger logger = pluginService.getPlugin().getLogger();

        if (command == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");

            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }

            TextUtils.printStackTrace();
            return;
        }

        runnableManager.waitSecond(sender, time, command);
    }


    public void openInventory(HumanEntity sender, String guiName) {
        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.openInventory(sender.getUniqueId(), guiName, 0);
    }
    public void openInventory(HumanEntity sender, String guiName, int page) {
        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.openInventory(sender.getUniqueId(), guiName, page);
    }

    public void openInventory(PlayerWrapper sender, String guiName, int page) {

        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.openInventory(sender.getUniqueId(), guiName, page);
    }

    public void openInventory(PlayerWrapper sender, String guiName) {

        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.openInventory(sender.getUniqueId(), guiName, 0);
    }

    public void sendMessagesLater(PlayerWrapper sender, int time, String... messages) {

        RunnableManager runnableManager = pluginService.getPlayerManager().getRunnableManager();

        Logger logger = pluginService.getPlugin().getLogger();

        if (messages == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");

            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
            TextUtils.printStackTrace();
            return;
        }
        runnableManager.waitSecond(sender, time, messages);
    }


    public void sendCommand(SenderWrapper sender, String command) {

        RunnableManager runnableManager = pluginService.getPlayerManager().getRunnableManager();

        Logger logger = pluginService.getPlugin().getLogger();

        if (command == null) {
            if (!configFile.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");

            } else {
                logger.info("Error - Could not find the path in config.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            }
            TextUtils.printStackTrace();
            return;
        }

        runnableManager.sendCommand(sender, command);
    }
}
