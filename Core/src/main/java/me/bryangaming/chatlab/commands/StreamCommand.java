package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.registry.FileLoader;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StreamCommand implements CommandClass {

    private final PluginService pluginService;

    public StreamCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = "stream")
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args) {

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        FileLoader files = pluginService.getFiles();

        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();


        if (args.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("stream", "<message>")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", args);

        boolean allowmode = false;

        String[] blockedRevisors;
        if (command.getBoolean("commands.stream.only-link")) {
            blockedRevisors = new String[]{"ALL"};
            if (message.startsWith("https://")) {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.substring(8).startsWith(string)) {
                        allowmode = true;
                        break;
                    }
                }

            } else {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.startsWith(string)) {
                        allowmode = true;
                        break;
                    }
                }
            }

            if (message.split(" ").length > 1) {
                allowmode = false;
            }

        } else {
            blockedRevisors = new String[]{"BotRevisor", "LinkRevisor"};
            if (message.contains(".")) {
                for (String string : command.getStringList("commands.stream.allowed-links")) {
                    if (message.contains(string)) {
                        allowmode = true;
                        break;
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
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(player, message, TextRevisorEnum.TEXT, blockedRevisors);
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
                return true;
            }

            message = textrevisorEvent.getMessageRevised();
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
