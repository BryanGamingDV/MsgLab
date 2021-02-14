package code.methods.commands;

import code.PluginService;
import code.data.UserData;
import code.methods.MethodService;
import code.methods.click.ChatMethod;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;

public class StaffChatMethod implements MethodService {

    private final PluginService pluginService;

    private final Map<UUID, UserData> cache;

    private String status;

    public StaffChatMethod(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache().getPlayerUUID();
    }
    public String getStatus(){
        return status;
    }

    public void toggleOption(UUID uuid){
        UserData usercache = cache.get(uuid);

        if (usercache.isStaffchatMode()) {
            usercache.toggleStaffChat(false);
            status = pluginService.getFiles().getCommand().getString("commands.staff-chat.player.variable-off");
            return;
        };

        usercache.toggleStaffChat(true);
        status = pluginService.getFiles().getCommand().getString("commands.staff-chat.player.variable-on");
    }

    public void enableOption(UUID uuid){
        cache.get(uuid).toggleStaffChat(true);
    }

    public void disableOption(UUID uuid){
        cache.get(uuid).toggleStaffChat(false);
    }

    public boolean isUsingStaffSymbol(AsyncPlayerChatEvent event) {

        Configuration command = pluginService.getFiles().getCommand();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        Player player = event.getPlayer();

        if (event.getMessage().startsWith(command.getString("commands.staff-chat.symbol"))){
            return playerMethod.hasPermission(player, "commands.staffchat.watch");
        }

        return false;
    }

    public void getStaffSymbol(AsyncPlayerChatEvent event){

        if (!(event.isCancelled())){
            event.setCancelled(true);
        }

        Player player = event.getPlayer();
        Configuration command = pluginService.getFiles().getCommand();

        ChatMethod chatMethod = pluginService.getPlayerMethods().getChatMethod();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        UserData playerStatus = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (!playerMethod.hasPermission(player, "commands.staffchat.watch")) {
            return;
        }

        if (playerStatus.isClickMode()) {
            chatMethod.unset(player.getUniqueId());
        }

        for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {
            if (!playerMethod.hasPermission(player, "commands.staffchat.watch")) {
                continue;
            }

            playerMethod.sendMessage(playeronline.getPlayer(), command.getString("commands.staff-chat.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", event.getMessage())
                    .replace(command.getString("commands.staff-chat.symbol"), ""));
        }
    }
}
