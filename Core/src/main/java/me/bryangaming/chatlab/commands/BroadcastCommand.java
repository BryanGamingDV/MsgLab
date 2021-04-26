package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(names = {"broadcast", "bc"})
public class BroadcastCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager playerMethod;

    private final Configuration command;
    private final Configuration messages;

    public BroadcastCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerManager().getSender();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player sender, @OptArg("") @Text String args) {

        UUID playeruuid = sender.getUniqueId();

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("broadcast", "<message>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", args);

        if (command.getBoolean("commands.broadcast.enable-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                return true;
            }

            message = textRevisorEvent.getMessageRevised();
        }

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            playerMethod.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.global")
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
            playerMethod.sendSound(sender, SoundEnum.RECEIVE_BROADCAST);
        }
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "broadcast");
        return true;
    }

    @Command(names = "-click")
    public boolean onClickSubCommand(@Sender Player sender) {
        ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();

        if (!playerMethod.hasPermission(sender, "commands.broadcast.click")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        clickChatManager.activateChat(sender.getUniqueId(), false);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "broadcast -click");
        return true;
    }

}
