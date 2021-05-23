package me.bryangaming.chatlab.common.managers.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.List;
import java.util.UUID;

public class MsgManager {

    private PluginService pluginService;

    public MsgManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void sendPrivateMessage(PlayerWrapper player, PlayerWrapper target, String message) {

        Configuration command = pluginService.getFiles().getCommandFile();
        Configuration players = pluginService.getFiles().getPlayersFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        String playerFormat = command.getString("commands.msg-reply.player")
                .replace("%player%", player.getName())
                .replace("%receiver%", target.getName());

        String targetFormat = command.getString("commands.msg-reply.arg-1")
                .replace("%player%", player.getName())
                .replace("%receiver%", target.getName());

        UUID playeruuid = player.getUniqueId();

        senderManager.sendMessage(player, playerFormat, message);
        senderManager.playSound(player, SoundEnum.ARGUMENT, "msg");

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (ignoredlist.contains(target.getName())) {
            return;
        }

        senderManager.sendMessage(target, targetFormat, message);
        senderManager.playSound(target, SoundEnum.RECEIVE_MSG);

    }


}
