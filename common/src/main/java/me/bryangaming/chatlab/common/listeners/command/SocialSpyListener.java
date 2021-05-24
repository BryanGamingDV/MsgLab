package me.bryangaming.chatlab.common.listeners.command;


import me.bryangaming.chatlab.api.Listener;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.SocialSpyEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;

import me.bryangaming.chatlab.common.wrapper.ServerWrapper;



public class SocialSpyListener implements Listener<SocialSpyEvent>{

    private PluginService pluginService;

    public SocialSpyListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void doAction(SocialSpyEvent socialSpyEvent) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        ServerWrapper.getData().getOnlinePlayers().forEach(player -> {
            UserData watcherSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

            if (!watcherSpy.isSocialSpyMode()) {
                return;
            }

            senderManager.sendMessage(player, socialSpyEvent.getMessage());
            senderManager.playSound(player, SoundEnum.RECEIVE_SOCIALSPY);
        });
    }
}
