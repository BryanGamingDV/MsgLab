package code.commands;

import code.PluginService;
import code.bukkitutils.SoundManager;
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

    private PluginService pluginService;

    public HelpopCommand(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @Command(names =  {"helpop", "ac"})
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args){

        ConfigManager files = pluginService.getFiles();

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        HelpOpMethod helpOpMethod = pluginService.getPlayerMethods().getHelpOpMethod();

        SoundManager sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();


        UUID playeruuid = player.getUniqueId();

        if (args.isEmpty()) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "helpop", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (args.equalsIgnoreCase("-list")){

            List<String> helpopList = new ArrayList<>();

            for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {
                UserData onlineCache = pluginService.getCache().getPlayerUUID().get(playeronline.getUniqueId());
                if (playeronline.hasPermission(config.getString("config.perms.helpop-watch")) && onlineCache.isPlayerHelpOp()){
                    helpopList.add(playeronline.getName());
                }
            }

            if (helpopList.isEmpty()) {
                playersender.sendMessage(player, messages.getString("error.helpop.anybody"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            playersender.sendMessage(player, command.getString("commands.helpop.list-helpop"));
            for (String helpopPlayers : helpopList) {
                playersender.sendMessage(player, "&8- &f" + helpopPlayers);
            }

            sound.setSound(playeruuid, "sounds.on-helpop.list");
            return true;
        }

        UserData playerCache = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (args.equalsIgnoreCase("-on")){
            if (!(player.hasPermission(config.getString("config.perms.helpop-watch")))){
                playersender.sendMessage(player, messages.getString("error.no-perms"));
                return true;
            }
            if (playerCache.isPlayerHelpOp()){
                playersender.sendMessage(player, messages.getString("error.helpop.activated"));
                return true;
            }

            helpOpMethod.enableOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.helpop.player.enabled"));
            sound.setSound(playeruuid, "sounds.on-helpop.enable");
            return true;
        }

        if (args.equalsIgnoreCase("-off")){
            if (!(player.hasPermission(config.getString("config.perms.helpop-watch")))){
                playersender.sendMessage(player, messages.getString("error.no-perms"));
                return true;
            }

            if (!(playerCache.isPlayerHelpOp())){
                playersender.sendMessage(player, messages.getString("error.helpop.unactivated"));
                return true;
            }

            helpOpMethod.disableOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.helpop.player.disabled"));
            sound.setSound(playeruuid, "sounds.on-helpop.disable");
            return true;
        }

        if (args.equalsIgnoreCase("-toggle")){
            if (!(player.hasPermission(config.getString("config.perms.helpop-watch")))){
                playersender.sendMessage(player, messages.getString("error.no-perms"));
                return true;
            }

            helpOpMethod.toggleOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.helpop.player.toggle")
                        .replace("%mode%", helpOpMethod.getStatus()));
            sound.setSound(playeruuid, "sounds.on-helpop.toggle");
            return true;
        }


        playersender.sendMessage(player, command.getString("commands.helpop.received")
                .replace("%player%", player.getName()));

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

            if (onlinePlayer.hasPermission(config.getString("config.perms.helpop-watch")) && onlineCache.isPlayerHelpOp()) {
                return;
            }

            playersender.sendMessage(onlinePlayer, command.getString("commands.helpop.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", finalMessage));
            sound.setSound(playeruuid, "sounds.on-helpop.receive-msg");
        });

        return true;
    }
}
