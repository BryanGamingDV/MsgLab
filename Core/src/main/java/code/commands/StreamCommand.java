package code.commands;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.bukkitutils.sound.SoundManager;
import code.managers.player.PlayerMessage;
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

    private final PluginService pluginService;

    public StreamCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = "stream")
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args) {

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        ModuleCheck moduleCheck = pluginService.getPathManager();

        ConfigManager files = pluginService.getFiles();

        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (args.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("stream", "<message>")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", args);

        boolean allowmode = false;

        if (command.getBoolean("commands.stream.only-link")) {
            if (message.startsWith("https://")) {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.substring(8).startsWith(string)) {
                        allowmode = true;
                        pluginService.getRevisorManager().setLevel(0);
                        break;
                    }
                }

            } else {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.startsWith(string)) {
                        allowmode = true;
                        pluginService.getRevisorManager().setLevel(0);
                        break;
                    }
                }
            }

            if (message.split(" ").length > 1) {
                allowmode = false;
            }

        } else {
            if (message.contains(".")) {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.contains(string)) {
                        allowmode = true;
                        pluginService.getRevisorManager().setLevel(1);
                    }
                }
            }
        }

        if (!allowmode) {
            playerMethod.sendMessage(player, messages.getString("error.stream.valid-link")
                    .replace("%message%", message));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (command.getBoolean("commands.stream.enable-revisor")) {
            RevisorManager revisorManager = pluginService.getRevisorManager();

            message = revisorManager.revisor(playeruuid, message);

            if (message == null) {
                return true;
            }
        }

        for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {
            playerMethod.sendMessage(playerOnline, command.getString("commands.stream.text")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
            playerMethod.sendSound(playerOnline, SoundEnum.RECEIVE_STREAM);
        }

        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "stream");
        return true;
    }

}
