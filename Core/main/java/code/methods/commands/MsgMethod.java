package code.methods.commands;

import code.Manager;
import code.bukkitutils.SoundManager;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MsgMethod {

    private Manager manager;

    public MsgMethod(Manager manager){
        this.manager = manager;
    }

    public void sendPrivateMessage(Player player, Player target, String message){

        Configuration command = manager.getFiles().getCommand();
        Configuration players = manager.getFiles().getPlayers();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();
        SoundManager sound = manager.getManagingCenter().getSoundManager();

        String playerFormat = command.getString("commands.msg-reply.player")
                .replace("%player%", player.getName())
                .replace("%arg-1%", target.getName());

        String targetFormat = command.getString("commands.msg-reply.arg-1")
                .replace("%player%", player.getName())
                .replace("%arg-1%", target.getName());

        UUID playeruuid = player.getUniqueId();

        playersender.sendMessage(player, playerFormat , message);
        sound.setSound(playeruuid, "sounds.on-message");

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (!(ignoredlist.contains(target.getName()))) {
            UUID targetuuid = target.getUniqueId();

            playersender.sendMessage(target , targetFormat , message);
            sound.setSound(targetuuid, "sounds.on-receive.msg");
        }

    }

}
