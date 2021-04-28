package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.SendDataEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
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
