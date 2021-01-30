package code.utils.module;


import code.Manager;
import code.bukkitutils.SoundManager;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.List;

public class ModuleCheck {

    private Manager manager;
    private Configuration config;

    public ModuleCheck(Manager manager) {
        this.manager = manager;
        this.config = manager.getFiles().getConfig();
    }

    public boolean isCommandEnabled(String commandName) {

        boolean bmsgCommand = false;

        for (String allCommands : manager.getListManager().getCommands()){
            if (allCommands.equalsIgnoreCase(commandName)){
                bmsgCommand = true;
                break;
            }
        }

        if (!bmsgCommand){
            return true;
        }

        List<String> commandFile = config.getStringList("config.modules.enabled-commands");
        for (String commandEnabledCmds : commandFile) {
            if (commandEnabledCmds.equalsIgnoreCase(commandName)) {
                return true;
            }
        }
        return false;
    }

    public void sendDisableMessage(Player player, String command){

        PlayerMessage sender = manager.getPlayerMethods().getSender();
        SoundManager sound = manager.getManagingCenter().getSoundManager();

        Configuration messages = manager.getFiles().getMessages();

        sender.sendMessage(player, messages.getString("error.command-disabled")
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