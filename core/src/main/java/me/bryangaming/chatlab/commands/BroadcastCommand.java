package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.click.ClickType;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

@Command(names = {"broadcast", "bc"})
public class BroadcastCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;

    private final Configuration configFile;
    private final Configuration messagesFile;

    public BroadcastCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player sender, @OptArg("") @Text String senderMessage) {

        if (senderMessage.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("broadcast", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
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

        if (!configFile.getBoolean("options.bungeecord")) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                senderManager.sendMessage(onlinePlayer, messagesFile.getString("broadcast.text.global")
                        .replace("%player%", sender.getName())
                        .replace("%message%", message));
                senderManager.playSound(sender, SoundEnum.RECEIVE_BROADCAST);
            }
        }else{
            pluginService.getRedisConnection().sendMessage("chatlab", MessageType.BROADCAST, messagesFile.getString("broadcast.text.global")
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
        }
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "broadcast");
        return true;
    }

    @Command(names = "-click")
    public boolean onClickSubCommand(@Sender Player sender) {
        ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();

        if (!senderManager.hasPermission(sender, "broadcast","global.click")) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        clickChatManager.activateChat(sender.getUniqueId(), ClickType.GLOBAL);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "broadcast -click");
        return true;
    }

}
