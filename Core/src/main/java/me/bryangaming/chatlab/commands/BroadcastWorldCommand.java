package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(names = {"broadcastworld", "bcw", "bcworld"})
public class BroadcastWorldCommand implements CommandClass {

    private final PluginService pluginService;

    private final ClickChatManager clickChatManager;
    private final PlayerMessage playerMethod;

    private final Configuration command;
    private final Configuration messages;

    public BroadcastWorldCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.clickChatManager = pluginService.getPlayerMethods().getChatManagent();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onMainSubCommand(@Sender Player sender, @OptArg("") @Text String args) {

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("broadcastworld", "<message>")));
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

        for (Player onlinePlayer : clickChatManager.getWorldChat(sender)) {
            playerMethod.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.world")
                    .replace("%world%", sender.getWorld().getName())
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
            playerMethod.sendSound(sender, SoundEnum.RECEIVE_BROADCASTWORLD);
        }
        return true;
    }

    @Command(names = "-click")
    public boolean onClickSubCommand(@Sender Player sender) {

        if (!playerMethod.hasPermission(sender, "commands.broadcastworld.click")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;
        }

        clickChatManager.activateChat(sender.getUniqueId(), true);
        return true;

    }

}
