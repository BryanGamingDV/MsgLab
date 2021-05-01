package me.bryangaming.chatlab.events;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;

public class ChatClickEvent implements Listener {

    private final PluginService pluginService;

    public ChatClickEvent(PluginService pluginService) {
        this.pluginService = pluginService;

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        UserData userData = pluginService.getCache().getUserDatas().get(uuid);
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        if (!(userData.isClickMode())) {
            return;
        }

        List<String> clickchat = userData.getClickChat();
        ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();

        Configuration command = pluginService.getFiles().getCommandFile();

        if (event.getMessage().startsWith("-cancel")) {
            clickChatManager.unset(uuid);
            return;
        }

        StaffChatManager staffChatManagerMethod = pluginService.getPlayerManager().getStaffChatMethod();

        if (staffChatManagerMethod.isUsingStaffSymbol(event)) {
            staffChatManagerMethod.getStaffSymbol(event);
            return;
        }

        event.setCancelled(true);

        if (clickchat.size() < 1) {
            clickchat.add(event.getMessage());
            senderManager.sendMessage(event.getPlayer(), command.getString("commands.broadcast.mode.selected.message")
                    .replace("%message%", clickchat.get(0)));
            clickChatManager.setAgain(uuid);
            return;
        }

        if (clickchat.size() < 2) {
            clickchat.add(event.getMessage());
            senderManager.sendMessage(event.getPlayer(), command.getString("commands.broadcast.mode.selected.command")
                    .replace("%command%", clickchat.get(1)));
            clickChatManager.setAgain(uuid);
            return;
        }

        if (clickchat.size() < 3) {

            if (event.getMessage().startsWith("-now")) {
                clickchat.add("1");
            } else {
                clickchat.add(event.getMessage());
            }

            senderManager.sendMessage(event.getPlayer(), command.getString("commands.broadcast.mode.selected.cooldown")
                    .replace("%cooldown%", clickchat.get(2)));


            userData.toggleClickMode(false);
            clickChatManager.setAgain(uuid);

        }

    }
}
