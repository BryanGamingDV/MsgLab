package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Option;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;

public class StaffChatManager implements Option {

    private final PluginService pluginService;

    private final Map<UUID, UserData> cache;

    private String status;

    public StaffChatManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.cache = pluginService.getCache().getUserDatas();
    }

    public String getStatus() {
        return status;
    }

    public void toggleOption(UUID uuid) {
        UserData userData = cache.get(uuid);

        if (userData.isStaffchatMode()) {
            userData.toggleStaffChat(false);
            status = pluginService.getFiles().getCommandFile().getString("commands.staff-chat.player.variable-off");
            return;
        }
        ;

        userData.toggleStaffChat(true);
        status = pluginService.getFiles().getCommandFile().getString("commands.staff-chat.player.variable-on");
    }

    public void enableOption(UUID uuid) {
        cache.get(uuid).toggleStaffChat(true);
    }

    public void disableOption(UUID uuid) {
        cache.get(uuid).toggleStaffChat(false);
    }

    public boolean isUsingStaffSymbol(AsyncPlayerChatEvent event) {

        Configuration command = pluginService.getFiles().getCommandFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Player player = event.getPlayer();

        if (event.getMessage().startsWith(command.getString("commands.staff-chat.symbol"))) {
            return senderManager.hasPermission(player, "commands.staffchat.watch");
        }

        return false;
    }

    public void getStaffSymbol(AsyncPlayerChatEvent event) {

        if (!(event.isCancelled())) {
            event.setCancelled(true);
        }

        Player player = event.getPlayer();
        Configuration command = pluginService.getFiles().getCommandFile();

        ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        UserData playerStatus = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (!senderManager.hasPermission(player, "commands.staffchat.watch")) {
            return;
        }

        if (playerStatus.isClickMode()) {
            clickChatManager.unset(player.getUniqueId());
        }

        for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {
            if (!senderManager.hasPermission(player, "commands.staffchat.watch")) {
                continue;
            }

            senderManager.sendMessage(playeronline, command.getString("commands.staff-chat.message")
                    .replace("%player%", player.getName())
                    .replace("%message%", event.getMessage())
                    .replace(command.getString("commands.staff-chat.symbol"), ""));
        }
    }
}
