package code.utils.module;


import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class ModuleCheck {

    private PluginService pluginService;
    private Configuration config;

    public ModuleCheck(PluginService pluginService) {
        this.pluginService = pluginService;
        this.config = pluginService.getFiles().getConfig();
    }

    public boolean isPluginCommand(String commandName){
        boolean bmsgCommand = false;

        for (String allCommands : pluginService.getListManager().getCommands()){
            if (allCommands.equalsIgnoreCase(StringUtils.remove(commandName, "/"))){
                bmsgCommand = true;
                break;
            }
        }

        return bmsgCommand;
    }

    public boolean isCommandDisabledInCooldown(String message){

        Configuration utils = pluginService.getFiles().getBasicUtils();

        for (String disabledCmds : utils.getStringList("chat.cooldown.cmd.disabled-cmds")){
            if (disabledCmds.equalsIgnoreCase(StringUtils.remove(message, "/"))){
                return true;
            }
        }

        return false;
    }
    public boolean isCommandEnabled(String commandName) {

        List<String> commandFile = config.getStringList("config.modules.enabled-commands");
        for (String commandEnabledCmds : commandFile) {
            if (commandEnabledCmds.equalsIgnoreCase(StringUtils.remove(commandName, "/"))){
                return true;
            }
        }
        return false;
    }

    public boolean IsCommandEnabledInMc(String commandName){

        for (String commands : pluginService.getListManager().getCommands()){
            if (commandName.equalsIgnoreCase(commandName)){
                return true;
            }
        }
        return false;
    }

    public void sendDisableMessage(Player player, String command){

        PlayerMessage sender = pluginService.getPlayerMethods().getSender();
        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();

        Configuration messages = pluginService.getFiles().getMessages();

        sender.sendMessage(player, messages.getString("error.module.command-disabled")
                .replace("%player%", player.getName())
                .replace("%command%", command));
        sender.sendMessage(player, "&e[!] &8| &fYou need to restart the server to activate o unactivate the command.");
        sound.setSound(player.getUniqueId(), "sounds.error");

    }

    public boolean isOptionEnabled(String optionName) {
        List<String> optionFile = config.getStringList("config.modules.enabled-options");
        for (String optionEnabledOptions : optionFile) {
            if (optionEnabledOptions.equalsIgnoreCase(optionName)) {
                return true;
            }
        }
        return false;
    }

    public String getUsage(String command, String... args){

        StringBuilder message = new StringBuilder();
        for (String arg : args){
            if (!(arg.contains(","))) {
                message.append(arg).append(" ");
                continue;
            }

            message.append("[").append(arg).append("] ");
        }

        return "/" +  command + " " + message.toString();

    }
}