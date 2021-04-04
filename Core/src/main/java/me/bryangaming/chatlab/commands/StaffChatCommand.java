package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.utils.Configuration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import me.bryangaming.chatlab.managers.commands.StaffChatMethod;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(names = {"sc", "staffchat"})
public class StaffChatCommand implements CommandClass {

    private PluginService pluginService;

    private PlayerMessage playerMethod;
    private StaffChatMethod staffChatMethod;

    private Configuration command;
    private Configuration messages;

    public StaffChatCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.staffChatMethod = pluginService.getPlayerMethods().getStaffChatMethod();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player player, @OptArg("") String args) {

        if (args.isEmpty()) {
            staffChatMethod.toggleOption(player.getUniqueId());
            playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.toggle")
                    .replace("%mode%", staffChatMethod.getStatus()));
            playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy");
            return true;
        }

        String message = String.join(" ", args);
        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            if (!playerMethod.hasPermission(onlinePlayer, "commands.staffchat.main")) {
                return;
            }

            playerMethod.sendMessage(onlinePlayer, command.getString("commands.staff-chat.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        });
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy");
        return true;
    }

    @Command(names = "-on")
    public boolean onOnSubCommand(@Sender Player player) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.isStaffchatMode()) {
            playerMethod.sendMessage(player, messages.getString("error.staff-chat.activated"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        staffChatMethod.enableOption(player.getUniqueId());
        playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.enabled"));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy -on");
        return true;
    }


    @Command(names = "-off")
    public boolean onOffSubCommand(@Sender Player player) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (!(userData.isStaffchatMode())) {
            playerMethod.sendMessage(player, messages.getString("error.staff-chat.unactivated"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        staffChatMethod.disableOption(player.getUniqueId());
        playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.disabled"));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy -off");
        return true;
    }

    @Command(names = "-toggle")
    public boolean onToggleSubCommand(@Sender Player player) {
        staffChatMethod.toggleOption(player.getUniqueId());
        playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.toggle")
                .replace("%mode%", staffChatMethod.getStatus()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy -toggle");
        return true;
    }
}
