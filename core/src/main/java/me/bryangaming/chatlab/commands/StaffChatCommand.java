package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
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
    private final StaffChatManager staffChatManagerMethod;

    private final Configuration commandFile;
    private final Configuration messagesFile;

    public StaffChatCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.staffChatManagerMethod = pluginService.getPlayerManager().getStaffChatMethod();

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player player, @OptArg("") String args) {

        if (args.isEmpty()) {
            staffChatManagerMethod.toggleOption(player.getUniqueId());
            senderManager.sendMessage(player, commandFile.getString("commands.staff-chat.player.toggle")
                    .replace("%mode%", staffChatManagerMethod.getStatus()));
            senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy");
            return true;
        }

        String message = String.join(" ", args);
        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            if (!senderManager.hasPermission(onlinePlayer, "commands.staffchat.main")) {
                return;
            }

            senderManager.sendMessage(onlinePlayer, commandFile.getString("commands.staff-chat.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        });
        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy") ;
        return true;
    }

    @Command(names = "-on")
    public boolean onOnSubCommand(@Sender Player player) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.isStaffchatMode()) {
            senderManager.sendMessage(player, messagesFile.getString("error.staff-chat.activated"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        staffChatManagerMethod.enableOption(player.getUniqueId());
        senderManager.sendMessage(player, commandFile.getString("commands.staff-chat.player.enabled"));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy -on");
        return true;
    }


    @Command(names = "-off")
    public boolean onOffSubCommand(@Sender Player player) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (!(userData.isStaffchatMode())) {
            senderManager.sendMessage(player, messagesFile.getString("error.staff-chat.unactivated"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        staffChatManagerMethod.disableOption(player.getUniqueId());
        senderManager.sendMessage(player, commandFile.getString("commands.staff-chat.player.disabled"));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy -off");
        return true;
    }

    @Command(names = "-toggle")
    public boolean onToggleSubCommand(@Sender Player player) {
        staffChatManagerMethod.toggleOption(player.getUniqueId());
        senderManager.sendMessage(player, commandFile.getString("commands.staff-chat.player.toggle")
                .replace("%mode%", staffChatManagerMethod.getStatus()));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy -toggle");
        return true;
    }
}
