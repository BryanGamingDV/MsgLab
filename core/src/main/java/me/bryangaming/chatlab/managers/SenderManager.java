package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.managers.sound.SoundManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

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


    public boolean hasUtilsPermission(Player player, String path) {

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

    public boolean hasPermission(CommandSender sender, String path) {
        if (!(sender instanceof Player)) {
            return true;
        }

        return hasPermission((Player) sender, path);
    }

    public boolean hasPermission(Player player, String path) {

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


    public void sendMessage(CommandSender sender, String path) {

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

        if (!(sender instanceof Player)) {
            System.out.println(path.replace("%newline%", "\n"));
            return;
        }

        Audience player = pluginService.getPlugin().getBukkitAudiences().player((Player) sender);
        player.sendMessage(TextUtils.convertTextToComponent((Player) sender, path));

    }


    public void sendMessage(Player sender, String path) {

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

    public void sendMessageTo(List<Player> playerList, String path) {

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

        for (Player online : playerList) {
            Audience player = pluginService.getPlugin().getBukkitAudiences().player(online);
            player.sendMessage(TextUtils.convertTextToComponent(online, path));
        }
    }

    public void sendMessage(Player sender, String path, String message) {
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

    public void sendMessage(Player sender, List<String> messages) {
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


    public void playSound(CommandSender sender, SoundEnum soundType) {
        if (!(sender instanceof Player)){
            return;
        }

        playSound((Player) sender, soundType);
    }

    public void playSound(Player player, SoundEnum soundType) {

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


    public SenderManager playSound(CommandSender sender, SoundEnum soundType, String command) {

        if (!(sender instanceof Player)){
            return this;
        }

        playSound((Player) sender, soundType, command);
        return this;
    }
    public SenderManager playSound(Player player, SoundEnum soundType, String command) {

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

    public void sendMessageLater(Player sender, int time, String command) {

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

    public void openInventory(Player sender, String guiName, int page) {

        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.openInventory(sender.getUniqueId(), guiName, page);
    }

    public void openInventory(Player sender, String guiName) {

        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.openInventory(sender.getUniqueId(), guiName, 0);
    }

    public void sendMessagesLater(Player sender, int time, String... messages) {

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


    public void sendCommand(CommandSender sender, String command) {

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
