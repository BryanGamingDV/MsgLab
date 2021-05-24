package me.bryangaming.chatlab.common.listeners.command;

import me.bryangaming.chatlab.api.Listener;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.HelpOpEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;


public class HelpOpListener implements Listener<HelpOpEvent> {


    private PluginService pluginService;

    public HelpOpListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void doAction(HelpOpEvent helpopEvent) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration command = pluginService.getFiles().getCommandFile();

        ServerWrapper.getData().getOnlinePlayers().forEach(onlinePlayer -> {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (!senderManager.hasPermission(onlinePlayer, "commands.helpop.watch") || !onlineCache.isPlayerHelpOp()) {
                return;
            }

            senderManager.sendMessage(onlinePlayer, command.getString("commands.helpop.message")
                    .replace("%player%", onlinePlayer.getName())
                    .replace("%message%", helpopEvent.getMessage()));
            senderManager.playSound(onlinePlayer, SoundEnum.RECEIVE_HELPOP);
        });
    }
}