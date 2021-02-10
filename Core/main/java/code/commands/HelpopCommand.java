package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.data.UserData;
import code.methods.commands.HelpOpMethod;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.revisor.RevisorManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HelpopCommand implements CommandClass{

    private final PluginService pluginService;

    public HelpopCommand(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @Command(names =  {"helpop", "ac"})
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String args){

        ConfigManager files = pluginService.getFiles();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        HelpOpMethod helpOpMethod = pluginService.getPlayerMethods().getHelpOpMethod();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();


        UUID playeruuid = sender.getUniqueId();

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "helpop", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (args.equalsIgnoreCase("-list")){

            List<String> helpopList = new ArrayList<>();

            for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {

                UserData onlineCache = pluginService.getCache().getPlayerUUID().get(playeronline.getUniqueId());

                if (playerMethod.hasPermission(sender, "commands.helpop.watch") && onlineCache.isPlayerHelpOp()){
                    helpopList.add(playeronline.getName());
                }
            }

            if (helpopList.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.helpop.anybody"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.helpop.list-helpop"));
            for (String helpopPlayers : helpopList) {
                playerMethod.sendMessage(sender, "&8- &f" + helpopPlayers);
            }

            sound.setSound(playeruuid, "sounds.on-helpop.list");
            return true;
        }

        UserData playerCache = pluginService.getCache().getPlayerUUID().get(sender.getUniqueId());

        if (args.equalsIgnoreCase("-on")){
            if (!(playerMethod.hasPermission(sender, "commands.helpop.watch"))){
                playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
                return true;
            }
            if (playerCache.isPlayerHelpOp()){
                playerMethod.sendMessage(sender, messages.getString("error.helpop.activated"));
                return true;
            }

            helpOpMethod.enableOption(playeruuid);
            playerMethod.sendMessage(sender, command.getString("commands.helpop.sender.enabled"));
            sound.setSound(playeruuid, "sounds.on-helpop.enable");
            return true;
        }

        if (args.equalsIgnoreCase("-off")){
            if (!(playerMethod.hasPermission(sender, "commands.helpop.watch"))){
                playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
                return true;
            }

            if (!(playerCache.isPlayerHelpOp())){
                playerMethod.sendMessage(sender, messages.getString("error.helpop.unactivated"));
                return true;
            }

            helpOpMethod.disableOption(playeruuid);
            playerMethod.sendMessage(sender, command.getString("commands.helpop.sender.disabled"));
            sound.setSound(playeruuid, "sounds.on-helpop.disable");
            return true;
        }

        if (args.equalsIgnoreCase("-toggle")){
            if (!(playerMethod.hasPermission(sender, "commands.helpop.watch"))){
                playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
                return true;
            }

            helpOpMethod.toggleOption(playeruuid);
            playerMethod.sendMessage(sender, command.getString("commands.helpop.sender.toggle")
                        .replace("%mode%", helpOpMethod.getStatus()));
            sound.setSound(playeruuid, "sounds.on-helpop.toggle");
            return true;
        }


        playerMethod.sendMessage(sender, command.getString("commands.helpop.received")
                .replace("%sender%", sender.getName()));

        String message = String.join(" ", args);

        if (command.getBoolean("commands.helpop.enable-revisor")){
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        String finalMessage = message;

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            UserData onlineCache = pluginService.getCache().getPlayerUUID().get(onlinePlayer.getUniqueId());

            if (!playerMethod.hasPermission(onlinePlayer, "commands.helpop.watch") && onlineCache.isPlayerHelpOp()) {
                return;
            }

            playerMethod.sendMessage(onlinePlayer, command.getString("commands.helpop.message")
                    .replace("%sender%", sender.getName())
                    .replace("%message%", finalMessage));
            sound.setSound(playeruuid, "sounds.on-helpop.receive-msg");
        });

        return true;
    }
}
