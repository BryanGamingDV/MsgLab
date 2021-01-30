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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StreamCommand implements CommandClass {

    private final Manager manager;

    public StreamCommand(Manager manager) {
        this.manager = manager;
    }

    @Command(names = "stream")
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args){

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        ModuleCheck moduleCheck = manager.getPathManager();
        SoundManager sound = manager.getManagingCenter().getSoundManager();

        ConfigManager files = manager.getFiles();

        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (!(player.hasPermission(config.getString("config.perms.stream")))){
            playersender.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        if (args.isEmpty()){
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("stream", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        String message = String.join(" ", args);

        boolean allowmode = false;

        if (command.getBoolean("commands.stream.only-link")) {
            if (message.startsWith("https://")) {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.substring(8).startsWith(string)) {
                        allowmode = true;
                        manager.getRevisorManager().setLevel(0);
                        break;
                    }
                }

            } else {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.startsWith(string)) {
                        allowmode = true;
                        manager.getRevisorManager().setLevel(0);
                        break;
                    }
                }
            }

            if (message.split(" ").length > 1){
                allowmode = false;
            }

        }else{
            if (message.contains(".")) {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.contains(string)) {
                        allowmode = true;
                        manager.getRevisorManager().setLevel(1);
                    }
                }
            }
        }

        if (!allowmode){
            playersender.sendMessage(player, messages.getString("error.stream.valid-link")
                    .replace("%message%", message));
            return true;
        }

        if (command.getBoolean("commands.stream.enable-revisor")){
            RevisorManager revisorManager = manager.getRevisorManager();

            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()){
            playersender.sendMessage(playerOnline, command.getString("commands.stream.text")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
            sound.setSound(playerOnline.getUniqueId(), "sounds.on-receive.stream");
        }


        return true;
    }

}
