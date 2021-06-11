package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.redis.MessageType;
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
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String senderMessage) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration configFile = pluginService.getFiles().getConfigFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        if (senderMessage.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("stream", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", senderMessage);

        boolean validLink = false;

        String[] blockedFilters;
        if (configFile.getBoolean("module.stream.only-link")) {
            blockedFilters = new String[]{"ALL"};
            if (message.startsWith("https://")) {
                for (String allowedLink : configFile.getStringList("module.stream.allowed-links")) {
                    if (message.substring(8).startsWith(allowedLink)) {
                        validLink = true;
                        break;
                    }
                }

            } else {
                for (String allowedLink : configFile.getStringList("module.stream.allowed-links")) {
                    if (message.startsWith(allowedLink)) {
                        validLink = true;
                        break;
                    }
                }
            }

            if (message.split(" ").length > 1) {
                validLink = false;
            }

        } else {
            blockedFilters = new String[]{"BotRevisor", "LinkRevisor"};
            if (message.contains(".")) {
                for (String string :configFile.getStringList("module.stream.allowed-links")) {
                    if (message.contains(string)) {
                        validLink = true;
                        break;
                    }
                }
            }
        }

        if (!validLink) {
            senderManager.sendMessage(sender, messagesFile.getString("stream.error.valid-link")
                    .replace("%message%", message));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (configFile.getBoolean("modules.stream.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT, blockedFilters);
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
                return true;
            }

            message = textrevisorEvent.getMessageRevised();
        }

        if (!configFile.getBoolean("options.bungeecord")) {
            for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {
                senderManager.sendMessage(playerOnline, configFile.getString("stream.text")
                        .replace("%player%", sender.getName())
                        .replace("%message%", message));
                senderManager.playSound(playerOnline, SoundEnum.RECEIVE_STREAM);
            }
        }else{
            pluginService.getRedisConnection().sendMessage("chatlab", MessageType.STREAM, configFile.getString("stream.text")
                    .replace("%player%", sender.getName())
                    .replace("%message%", message) );
        }

        senderManager.playSound(sender, SoundEnum.ARGUMENT, "stream");
        return true;
    }

}
