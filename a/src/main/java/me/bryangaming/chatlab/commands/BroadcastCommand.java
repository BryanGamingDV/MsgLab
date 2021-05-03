package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(names = {"broadcast", "bc"})
public class BroadcastCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;

    private final Configuration commandFile;
    private final Configuration messagesFile;

    public BroadcastCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player sender, @OptArg("") @Text String args) {

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("broadcast", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", args);

        if (commandFile.getBoolean("commands.broadcast.enable-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                return true;
            }

            message = textRevisorEvent.getMessageRevised();
        }

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            senderManager.sendMessage(onlinePlayer, commandFile.getString("commands.broadcast.text.global")
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
            senderManager.playSound(sender, SoundEnum.RECEIVE_BROADCAST);
        }
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "broadcast");
        return true;
    }

    @Command(names = "-click")
    public boolean onClickSubCommand(@Sender Player sender) {
        ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();

        if (!senderManager.hasPermission(sender, "commands.broadcast.click")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        clickChatManager.activateChat(sender.getUniqueId(), false);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "broadcast -click");
        return true;
    }

}
