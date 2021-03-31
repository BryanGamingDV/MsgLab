package code.managers.commands;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.bukkitutils.sound.SoundManager;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MsgMethod {

    private PluginService pluginService;

    public MsgMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void sendPrivateMessage(Player player, Player target, String message) {

        Configuration command = pluginService.getFiles().getCommand();
        Configuration players = pluginService.getFiles().getPlayers();

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

        String playerFormat = command.getString("commands.msg-reply.player")
                .replace("%player%", player.getName())
                .replace("%arg-1%", target.getName());

        String targetFormat = command.getString("commands.msg-reply.arg-1")
                .replace("%player%", player.getName())
                .replace("%arg-1%", target.getName());

        UUID playeruuid = player.getUniqueId();

        playersender.sendMessage(player, playerFormat, message);
        playersender.sendSound(player, SoundEnum.ARGUMENT, "msg");

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (ignoredlist.contains(target.getName())) {
            return;
        }

        playersender.sendMessage(target, targetFormat, message);
        playersender.sendSound(target, SoundEnum.RECEIVE_MSG);

    }


}
