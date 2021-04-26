package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.PartyData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.server.ChangeMode;
import me.bryangaming.chatlab.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.managers.group.GroupMethod;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitListener implements Listener {

    private final PluginService pluginService;

    public QuitListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        SenderManager player = pluginService.getPlayerManager().getSender();
        Configuration command = pluginService.getFiles().getCommand();

        GroupMethod groupMethod = pluginService.getPlayerManager().getGroupMethod();

        Player you = event.getPlayer();
        UserData playerStatus = pluginService.getCache().getUserDatas().get(you.getUniqueId());

        String playerRank = groupMethod.getJQGroup(you);

        if (playerStatus.getPartyID() > 0){
            PartyData partyData = pluginService.getServerData().getParty(playerStatus.getPartyID());

            for (UUID uuid : partyData.getPlayers()) {

                Player online = Bukkit.getPlayer(uuid);

                if (playerStatus.isPlayerLeader()){
                    player.sendMessage(online, command.getString("commands.party.on-left.player")
                            .replace("%player%", you.getName()));
                    continue;
                }
                player.sendMessage(online, command.getString("commands.party.on-left.leader")
                            .replace("%leader%", you.getName()));

            }

            if (playerStatus.isPlayerLeader()){
                pluginService.getServerData().deleteParty(partyData.getChannelID());
            }
        }
        playerStatus.resetStats();
        if (pluginService.getListManager().isEnabledOption("modules", "join_quit")) {
            Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, event.getPlayer(), playerRank, ChangeMode.QUIT));
        }

        if (pluginService.getListManager().isEnabledOption("commands", "reply")) {
            if (!playerStatus.hasRepliedPlayer()) {
                return;
            }

            UserData target = pluginService.getCache().getUserDatas().get(playerStatus.getRepliedPlayer());

            if (!target.hasRepliedPlayer()) {
                return;
            }

            if (target.hasRepliedPlayer(you.getUniqueId())) {
                target.setRepliedPlayer(null);
            }

            player.sendMessage(target.getPlayer(), command.getString("commands.msg-toggle.left")
                    .replace("%player%", target.getPlayer().getName())
                    .replace("%arg-1%", event.getPlayer().getName()));
        }


    }

}
