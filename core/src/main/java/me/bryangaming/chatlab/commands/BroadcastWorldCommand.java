package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.click.ClickType;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.*;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastWorldCommand implements CommandClass {


    private final ClickChatManager clickChatManager;
    private final SenderManager senderManager;

    private final Configuration configFile;
    private final Configuration messagesFile;

    public BroadcastWorldCommand(PluginService pluginService) {
        this.senderManager = pluginService.getPlayerManager().getSender();
        this.clickChatManager = pluginService.getPlayerManager().getChatManagent();

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }


    @Command(names = {"broadcastworld", "bcw", "bcworld"})
    public boolean onMainSubCommand(@Sender Player sender, @Text String senderMessage) {

        if (senderMessage.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("broadcastworld", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }


        if (senderMessage.startsWith("-click")) {
            if (!senderManager.hasPermission(sender, "broadcast", "world.click")) {
                senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
                return true;
            }

            clickChatManager.activateChat(sender.getUniqueId(), ClickType.WORLD);
            return true;
        }

        String message = String.join(" ", senderMessage);

        if (configFile.getBoolean("modules.broadcast.enable-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                return true;
            }

            message = textRevisorEvent.getMessageRevised();
        }

        for (Player onlinePlayer : sender.getWorld().getPlayers()) {
            senderManager.sendMessage(onlinePlayer, messagesFile.getString("broadcast.text.world")
                    .replace("%world%", sender.getWorld().getName())
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
            senderManager.playSound(sender, SoundEnum.RECEIVE_BROADCASTWORLD);
        }
        return true;
    }

}
