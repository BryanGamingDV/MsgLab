package code.methods.commands;

import code.Manager;
import code.cache.UserData;
import code.methods.MethodService;
import code.methods.click.ChatMethod;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class StaffChatMethod implements MethodService {

    private final Manager manager;

    private final Map<UUID, UserData> cache;

    private String status;

    public StaffChatMethod(Manager manager) {
        this.manager = manager;
        this.cache = manager.getCache().getPlayerUUID();
    }
    public String getStatus(){
        return status;
    }

    public void toggleOption(UUID uuid){
        UserData usercache = cache.get(uuid);

        if (usercache.isStaffchatMode()) {
            usercache.toggleStaffChat(false);
            status = manager.getFiles().getCommand().getString("commands.staff-chat.player.variable-off");
            return;
        };

        usercache.toggleStaffChat(true);
        status = manager.getFiles().getCommand().getString("commands.staff-chat.player.variable-on");
    }

    public void enableOption(UUID uuid){
        cache.get(uuid).toggleStaffChat(true);
    }

    public void disableOption(UUID uuid){
        cache.get(uuid).toggleStaffChat(false);
    }

    public boolean isUsingStaffSymbol(AsyncPlayerChatEvent event) {

        Configuration command = manager.getFiles().getCommand();

        Player player = event.getPlayer();

        if (event.getMessage().startsWith(command.getString("commands.staff-chat.symbol"))){
            return player.hasPermission("config.perms.staff-chat");
        }
        return false;
    }

    public void getStaffSymbol(AsyncPlayerChatEvent event){

        if (!(event.isCancelled())){
            event.setCancelled(true);
        }

        Player player = event.getPlayer();

        Configuration config = manager.getFiles().getConfig();
        Configuration command = manager.getFiles().getCommand();

        ChatMethod chatMethod = manager.getPlayerMethods().getChatMethod();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();
        UserData playerStatus = manager.getCache().getPlayerUUID().get(player.getUniqueId());

        if (!player.hasPermission("config.perms.staff-chat")) {
            return;
        }

        if (playerStatus.isClickMode()) {
            chatMethod.unset(player.getUniqueId());
        }

        for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission(config.getString("config.perms.staff-chat"))) {
                playersender.sendMessage(playeronline.getPlayer(), command.getString("commands.staff-chat.message")
                        .replace("%player%", player.getName())
                        .replace("%message%", event.getMessage())
                        .replace(command.getString("commands.staff-chat.symbol"), ""));
            }
        }
    }
}
