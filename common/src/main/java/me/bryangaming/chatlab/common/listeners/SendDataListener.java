package me.bryangaming.chatlab.common.listeners;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.events.SendDataEvent;
import me.bryangaming.chatlab.common.utils.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SendDataListener implements Listener {

    private PluginService pluginService;

    public SendDataListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onSend(SendDataEvent event){

        Configuration commandsFile = pluginService.getFiles().getCommandFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        if (senderManager.hasPermission(event.getPlayer(), "commands.helpop.watch")) {
            event.getUserData().toggleHelpOp(true);
        }

        event.getUserData().setChannelGroup(commandsFile.getString("commands.channel.config.default-channel"));
    }
}
