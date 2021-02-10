package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.data.UserData;
import code.methods.commands.StaffChatMethod;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.utils.Configuration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaffChatCommand  implements CommandClass{

    private PluginService pluginService;

    public StaffChatCommand(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @Command(names = {"sc", "staffchat"})
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args){

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        StaffChatMethod staffChatMethod = pluginService.getPlayerMethods().getStaffChatMethod();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ConfigManager files = pluginService.getFiles();

        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (args.isEmpty()) {
            staffChatMethod.toggleOption(playeruuid);
            playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.toggle")
                    .replace("%mode%", staffChatMethod.getStatus()));
            sound.setSound(playeruuid, "sounds.on-staff-chat.toggle");
            return true;
        }

        UserData playerCache = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (args.equalsIgnoreCase("-on")){
            if (playerCache.isStaffchatMode()){
                playerMethod.sendMessage(player, messages.getString("error.staff-chat.activated"));
                return true;
            }

            staffChatMethod.enableOption(playeruuid);
            playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.enabled"));
            sound.setSound(playeruuid, "sounds.on-staff-chat.enable");
            return true;
        }

        if (args.equalsIgnoreCase("-off")){
            if (!(playerCache.isStaffchatMode())){
                playerMethod.sendMessage(player, messages.getString("error.staff-chat.unactivated"));
                return true;
            }

            staffChatMethod.disableOption(playeruuid);
            playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.disabled"));
            sound.setSound(playeruuid, "sounds.on-staff-chat.disable");
            return true;
        }

        if (args.equalsIgnoreCase("-toggle")){
            staffChatMethod.toggleOption(playeruuid);
            playerMethod.sendMessage(player, command.getString("commands.staff-chat.player.toggle")
                    .replace("%mode%", staffChatMethod.getStatus()));
            sound.setSound(playeruuid, "sounds.on-staff-chat.toggle");
            return true;
        }

        String message = String.join(" ", args);
        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            if (playerMethod.hasPermission(onlinePlayer, "commands.staffchat.main")) {
                return;
            }

            playerMethod.sendMessage(onlinePlayer.getPlayer(), command.getString("commands.staff-chat.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        });
        return true;
    }
}
