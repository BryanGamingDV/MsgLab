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

    private final Configuration configFile;
    private final Configuration messagesFile;

    private final Map<UUID, UserData> cache;
    private final SenderManager senderManager;

    private String status;

    public StaffChatManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.senderManager = pluginService.getPlayerManager().getSender();

        this.cache = pluginService.getCache().getUserDatas();
    }

    public String getStatus() {
        return status;
    }

    public void toggleOption(UUID uuid) {
        UserData userData = cache.get(uuid);

        if (userData.isStaffchatMode()) {
            userData.toggleStaffChat(false);
            status = messagesFile.getString("staff-chat.player.variable-off");
            return;
        }
        ;

        userData.toggleStaffChat(true);
        status = messagesFile.getString("staff-chat.player.variable-on");
    }

    public void enableOption(UUID uuid) {
        cache.get(uuid).toggleStaffChat(true);
    }

    public void disableOption(UUID uuid) {
        cache.get(uuid).toggleStaffChat(false);
    }

    public boolean isUsingStaffSymbol(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        if (event.getMessage().startsWith(configFile.getString("modules.staff-chat.symbol"))) {
            return senderManager.hasPermission(player, "staff-chat", "watch");
        }

        return false;
    }

    public void sendToStaffPlayers(String playerName, String message){
        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            if (!senderManager.hasPermission(onlinePlayer, "staff-chat", "main")) {
                return;
            }

            senderManager.sendMessage(onlinePlayer, messagesFile.getString("staff-chat.message")
                    .replace("%player%", playerName)
                    .replace("%message%", message));
        });
    }
    public void getStaffSymbol(AsyncPlayerChatEvent event) {

        if (!(event.isCancelled())) {
            event.setCancelled(true);
        }

        Player player = event.getPlayer();

        ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();
        UserData senderData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (!senderManager.hasPermission(player, "staff-chat", "watch")){
            return;
        }

        if (senderData.isClickMode()) {
            clickChatManager.unset(player.getUniqueId());
        }

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (!senderManager.hasPermission(player, "staff-chat", "watch")){
                continue;
            }

            senderManager.sendMessage(onlinePlayer, messagesFile.getString("staff-chat.format")
                    .replace("%player%", player.getName())
                    .replace("%message%", event.getMessage())
                    .replace(messagesFile.getString("staff-chat.symbol"), ""));
        }
    }
}
