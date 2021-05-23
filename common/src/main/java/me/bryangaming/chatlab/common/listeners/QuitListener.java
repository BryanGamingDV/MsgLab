package me.bryangaming.chatlab.common.listeners;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.events.server.ChangeMode;
import me.bryangaming.chatlab.common.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.group.GroupManager;
import me.bryangaming.chatlab.common.utils.module.ModuleType;
import me.bryangaming.chatlab.common.data.PartyData;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.utils.Configuration;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
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

        PlayerWrapper player = event.getPlayer();

        Configuration commandFile = pluginService.getFiles().getCommandFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();
        String playerRank = groupManager.getJQGroup(player);

        UserData playerStatus = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (playerStatus.getPartyID() > 0){
            PartyData partyData = pluginService.getServerData().getParty(playerStatus.getPartyID());

            for (UUID uuid : partyData.getPlayers()) {

                PlayerWrapper online = Bukkit.getPlayer(uuid);

                if (playerStatus.isPlayerLeader()){
                    senderManager.sendMessage(online, commandFile.getString("commands.party.on-left.player")
                            .replace("%player%", player.getName()));
                    continue;
                }
                senderManager.sendMessage(online, commandFile.getString("commands.party.on-left.leader")
                            .replace("%leader%", player.getName()));

            }

            if (playerStatus.isPlayerLeader()){
                pluginService.getServerData().deleteParty(partyData.getChannelID());
            }
        }
        playerStatus.resetStats();
        if (pluginService.getListManager().isEnabledOption(ModuleType.MODULE, "join_quit")) {
            Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, event.getPlayer(), playerRank, ChangeMode.QUIT));
        }

        if (pluginService.getListManager().isEnabledOption(ModuleType.COMMAND, "reply")) {
            if (!playerStatus.hasRepliedPlayer()) {
                return;
            }

            UserData target = pluginService.getCache().getUserDatas().get(playerStatus.getRepliedPlayer());

            if (!target.hasRepliedPlayer()) {
                return;
            }

            if (target.hasRepliedPlayer(player.getUniqueId())) {
                target.setRepliedPlayer(null);
            }

            senderManager.sendMessage(target.getPlayer(), commandFile.getString("commands.msg-toggle.left")
                    .replace("%player%", target.getPlayer().getName())
                    .replace("%arg-1%", event.getPlayer().getName()));
        }


    }

}
