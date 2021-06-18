package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.redis.MessageType;
import me.bryangaming.chatlab.utils.Configuration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(names = {"staffchat", "sc"})
public class StaffChatCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;
    private final StaffChatManager staffChatManagerManager;

    private final Configuration configFile;
    private final Configuration messagesFile;

    public StaffChatCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.staffChatManagerManager = pluginService.getPlayerManager().getStaffChatManager();

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player sender, @OptArg("") String senderMessage) {

        if (senderMessage.isEmpty()) {
            staffChatManagerManager.toggleOption(sender.getUniqueId());
            senderManager.sendMessage(sender, messagesFile.getString("staff-chat.player.toggle")
                    .replace("%mode%", staffChatManagerManager.getStatus()));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "staffchat");
            return true;
        }

        String message = String.join(" ", senderMessage);

        if (configFile.getBoolean("options.redis.enabled")){
            pluginService.getRedisConnection().sendMessage("chatlab", MessageType.STAFFCHAT, sender.getName(), message);
        }else {
            Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

                if (!senderManager.hasPermission(onlinePlayer, "staff-chat", "main")) {
                    return;
                }

                senderManager.sendMessage(onlinePlayer, messagesFile.getString("staff-chat.format")
                        .replace("%player%", sender.getName())
                        .replace("%message%", message));
            });
        }
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "staffchat") ;
        return true;
    }

    @Command(names = "-on")
    public boolean onOnSubCommand(@Sender Player sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (userData.isStaffchatMode()) {
            senderManager.sendMessage(sender, messagesFile.getString("staff-chat.error.activated"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        staffChatManagerManager.enableOption(sender.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("staff-chat.player.enabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "staffchat -on");
        return true;
    }


    @Command(names = "-off")
    public boolean onOffSubCommand(@Sender Player sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(userData.isStaffchatMode())) {
            senderManager.sendMessage(sender, messagesFile.getString("staff-chat.error.unactivated"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        staffChatManagerManager.disableOption(sender.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("staff-chat.player.disabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "staffchat -off");
        return true;
    }

    @Command(names = "-toggle")
    public boolean onToggleSubCommand(@Sender Player sender) {
        staffChatManagerManager.toggleOption(sender.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("staff-chat.player.toggle")
                .replace("%mode%", staffChatManagerManager.getStatus()));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "staffchat -toggle");
        return true;
    }
}
