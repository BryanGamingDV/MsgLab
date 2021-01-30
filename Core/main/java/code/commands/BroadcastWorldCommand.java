package code.commands;

import code.Manager;
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
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.UUID;

public class BroadcastWorldCommand implements CommandClass {

    private final Manager manager;

    public BroadcastWorldCommand(Manager manager) {
        this.manager = manager;
    }

    @Command(names = {"broadcastworld", "bcw", "bcworld"})
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args) {

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        SoundManager sound = manager.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = manager.getPathManager();

        ConfigManager files = manager.getFiles();

        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (!(player.hasPermission(config.getString("config.perms.broadcast-world")))){
            playersender.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        if (args.isEmpty()) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "broadcastworld", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        ChatMethod chatMethod = manager.getPlayerMethods().getChatMethod();

        if (args.equalsIgnoreCase("-click")) {
            chatMethod.activateChat(playeruuid, true);
            return true;
        }

        String message = String.join(" ", args);

        if (command.getBoolean("commands.broadcast.enable-revisor")){
            RevisorManager revisorManager = manager.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        for (Player onlinePlayer : chatMethod.getWorldChat(player)) {
            playersender.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.world")
                    .replace("%world%", player.getWorld().getName())
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
            sound.setSound(onlinePlayer.getUniqueId(), "sounds.on-receive.broadcast-world");
        }
        return true;
    }

}
