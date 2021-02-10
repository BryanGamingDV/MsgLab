package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
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
import org.bukkit.entity.Player;

import java.util.UUID;

public class BroadcastWorldCommand implements CommandClass {

    private final PluginService pluginService;

    public BroadcastWorldCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = {"broadcastworld", "bcw", "bcworld"})
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String args) {

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        ConfigManager files = pluginService.getFiles();

        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "broadcastworld", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        ChatMethod chatMethod = pluginService.getPlayerMethods().getChatMethod();

        if (args.equalsIgnoreCase("-click")) {
            if (!playerMethod.hasPermission(sender, "commands.broadcast-world.click")){
                playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
                return true;
            }

            chatMethod.activateChat(playeruuid, true);
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

        for (Player onlinePlayer : chatMethod.getWorldChat(sender)) {
            playerMethod.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.world")
                    .replace("%world%", sender.getWorld().getName())
                    .replace("%sender%", sender.getName())
                    .replace("%message%", message));
            sound.setSound(onlinePlayer.getUniqueId(), "sounds.on-receive.broadcast-world");
        }
        return true;
    }

}
