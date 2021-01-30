package code.commands;

import code.PluginService;
import code.bukkitutils.SoundManager;
import code.methods.click.ChatMethod;
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

import java.util.UUID;

public class BroadcastCommand implements CommandClass {

    private final PluginService pluginService;

    public BroadcastCommand(PluginService pluginService){
        this.pluginService = pluginService;
    }


    @Command(names =  {"broadcast", "bc"})
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args){

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

        ModuleCheck moduleCheck = pluginService.getPathManager();
        SoundManager sound = pluginService.getManagingCenter().getSoundManager();

        ConfigManager files = pluginService.getFiles();

        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (!(player.hasPermission(config.getString("config.perms.broadcast")))){
            playersender.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        if (args.isEmpty()){
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("broadcast", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }


        ChatMethod chatMethod = pluginService.getPlayerMethods().getChatMethod();

        if (args.equalsIgnoreCase("-click")) {
            chatMethod.activateChat(playeruuid, false);
            return true;
        }

        String message = String.join(" ", args);

        if (command.getBoolean("commands.broadcast.enable-revisor")){
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()){
            playersender.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.global")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
            sound.setSound(onlinePlayer.getUniqueId(), "sounds.on-receive.broadcast");
        }

        return true;
    }

}
