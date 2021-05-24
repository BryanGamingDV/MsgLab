package me.bryangaming.chatlab.common.listeners;

import me.bryangaming.chatlab.api.Listener;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.events.SendDataEvent;
import me.bryangaming.chatlab.common.utils.Configuration;



public class SendDataListener implements Listener<SendDataEvent> {

    private PluginService pluginService;

    public SendDataListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    public void doAction(SendDataEvent event){

        Configuration commandsFile = pluginService.getFiles().getCommandFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        if (senderManager.hasPermission(event.getPlayer(), "commands.helpop.watch")) {
            event.getUserData().toggleHelpOp(true);
        }

        event.getUserData().setChannelGroup(commandsFile.getString("commands.channel.config.default-channel"));
    }
}
