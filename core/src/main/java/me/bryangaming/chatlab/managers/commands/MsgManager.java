package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.redis.MessageType;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MsgManager {

    private final PluginService pluginService;

    public MsgManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void sendBungeePrivateMessage(Player player, String target, String message) {
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        String playerFormat = messagesFile.getString("msg-reply.format.player")
                .replace("%player%", player.getName())
                .replace("%receiver%", target)
                .replace("%message%", message);
        pluginService.getRedisConnection().sendMessage("chatlab", MessageType.MSG, target, playerFormat);
    }

    public void sendPrivateMessage(Player player, Player target, String message) {

        Configuration messagesFile = pluginService.getFiles().getMessagesFile();
        Configuration players = pluginService.getFiles().getPlayersFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        String playerFormat = messagesFile.getString("msg-reply.format.player")
                .replace("%sender%", player.getName())
                .replace("%target%", target.getName());

        String targetFormat = messagesFile.getString("msg-reply.format.arg-1")
                .replace("%player%", player.getName())
                .replace("%sender%", target.getName());

        UUID playeruuid = player.getUniqueId();

        senderManager.sendMessage(player, playerFormat, message);
        senderManager.playSound(player, SoundEnum.ARGUMENT, "msg");

        List<String> ignoredPlayer = players.getStringList("players." + playeruuid + ".players-ignored");

        if (ignoredPlayer.contains(target.getName())) {
            return;
        }

        senderManager.sendMessage(target, targetFormat, message);
        senderManager.playSound(target, SoundEnum.RECEIVE_MSG);

    }


}
