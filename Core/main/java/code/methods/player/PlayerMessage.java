package code.methods.player;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.utils.Configuration;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.org.apache.bcel.generic.StackProducer;

import java.util.logging.Logger;

public class PlayerMessage{

    private final PluginService pluginService;

    private final Configuration config;

    public PlayerMessage(PluginService pluginService){
        this.pluginService = pluginService;
        this.config = pluginService.getFiles().getConfig();
    }

    public boolean sendSound(Player player, String path){

        SoundCreator soundCreator = pluginService.getManagingCenter().getSoundManager();
        Logger logger = pluginService.getPlugin().getLogger();

        try{
            logger.info("Error - The path that should send you is null.");
            logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");
            config.getConfigurationSection(path);

        } catch (NullPointerException nullPointerException){
            sendLines(nullPointerException);

        }

        soundCreator.setSound(player.getUniqueId(), "sounds." + path);
        return false;
    }


    public boolean hasPermission(Player player, String path){

        Logger logger = pluginService.getPlugin().getLogger();
        String permission = config.getString("perms." + path);

        if (permission == null) {

            if (config.getString("perms." + StringUtils.remove(path, ".main")) == null) {

                if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                    logger.info("Please change the configuration section! Your config is old.");

                } else {
                    logger.info("Error - The path that should send you is null.");
                    logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

                }
            }else{
                permission = config.getString("perms." + StringUtils.remove(path, ".main"));
            }

        }else{
            if (permission.equalsIgnoreCase("none")){
                return true;
            }
        }


        try{
            return player.hasPermission(permission);

        } catch (NullPointerException nullPointerException){
            sendLines(nullPointerException);

        }
        return false;
    }

    public void sendMessage(CommandSender sender, String path, String message) {
        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - The path that should send you is null.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try{
                path = getPlaceholders(sender, path);
            } catch (NullPointerException nullPointerException){
                sendLines(nullPointerException);
                return;
            }
        }

        try{
            sender.spigot().sendMessage(getMessage(path, message));
        } catch (NullPointerException nullPointerException){
            sendLines(nullPointerException);
        }
    }

    public void sendMessage(Player player, String path) {

        Logger logger = pluginService.getPlugin().getLogger();
        if (path == null) {
            if (!config.getString("version", "1.0").equalsIgnoreCase(pluginService.getPlugin().getDescription().getVersion())) {
                logger.info("Please change the configuration section! Your config is old.");
            } else {
                logger.info("Error - The path that should send you is null.");
                logger.info("Please copy the lines and post in: https://discord.gg/wpSh4Bf4Es");

            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try{
                path = getPlaceholders(player, path);
            } catch (NullPointerException nullPointerException){
                sendLines(nullPointerException);
                return;
            }
        }

        try{
            player.spigot().sendMessage(getMessage(path));
        } catch (NullPointerException nullPointerException){
            sendLines(nullPointerException);
        }
    }

    public BaseComponent[] getMessage(String message) {
        message = pluginService.getStringFormat().replaceString(message);
        
        return PlayerStatic.convertText(PlayerStatic.setColor(message));
    }


    public BaseComponent[] getMessage(String path, String message) {
        path = pluginService.getStringFormat().replaceString(path);
        message = pluginService.getStringFormat().replaceString(message);

        return PlayerStatic.convertText(PlayerStatic.setColor(path, message));
    }

    private String getPlaceholders(CommandSender sender, String path){

        Player player = (Player) sender;
        return PlayerStatic.setVariables(player, path);
    }

    public void sendLines(NullPointerException nullPointerException){
        Logger logger = pluginService.getPlugin().getLogger();

        logger.info("Code line:");
        for (StackTraceElement stackTraceElement : nullPointerException.getStackTrace()){

            String errorLine = stackTraceElement.toString();

            if (errorLine.contains("java.")){
                break;
            }

            if (!errorLine.contains("code.")){
                continue;
            }

            logger.info("- " + stackTraceElement.toString());

        }
    }
}
