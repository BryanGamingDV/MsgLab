package code.commands;

import code.Manager;
import code.bukkitutils.SoundManager;
import code.cache.UserData;
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

    private Manager manager;

    public StaffChatCommand(Manager manager){
        this.manager = manager;
    }

    @Command(names = {"sc", "staffchat"})
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String args){
        ConfigManager files = manager.getFiles();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();
        StaffChatMethod staffChatMethod = manager.getPlayerMethods().getStaffChatMethod();

        SoundManager sound = manager.getManagingCenter().getSoundManager();

        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (!(player.hasPermission(config.getString("config.perms.staff-chat")))){
            playersender.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        if (args.isEmpty()) {
            staffChatMethod.toggleOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.staff-chat.player.toggle")
                    .replace("%mode%", staffChatMethod.getStatus()));
            sound.setSound(playeruuid, "sounds.on-staff-chat.toggle");
            return true;
        }

        UserData playerCache = manager.getCache().getPlayerUUID().get(player.getUniqueId());

        if (args.equalsIgnoreCase("-on")){
            if (playerCache.isStaffchatMode()){
                playersender.sendMessage(player, messages.getString("error.staff-chat.activated"));
                return true;
            }

            staffChatMethod.enableOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.staff-chat.player.enabled"));
            sound.setSound(playeruuid, "sounds.on-staff-chat.enable");
            return true;
        }

        if (args.equalsIgnoreCase("-off")){
            if (!(playerCache.isStaffchatMode())){
                playersender.sendMessage(player, messages.getString("error.staff-chat.unactivated"));
                return true;
            }

            staffChatMethod.disableOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.staff-chat.player.disabled"));
            sound.setSound(playeruuid, "sounds.on-staff-chat.disable");
            return true;
        }

        if (args.equalsIgnoreCase("-toggle")){
            staffChatMethod.toggleOption(playeruuid);
            playersender.sendMessage(player, command.getString("commands.staff-chat.player.toggle")
                    .replace("%mode%", staffChatMethod.getStatus()));
            sound.setSound(playeruuid, "sounds.on-staff-chat.toggle");
            return true;
        }

        String message = String.join(" ", args);

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.hasPermission(config.getString("config.perms.staff-chat"))) {
                return;
            }

            playersender.sendMessage(onlinePlayer.getPlayer(), command.getString("commands.staff-chat.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        });
        return true;
    }
}
