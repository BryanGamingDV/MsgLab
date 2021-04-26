package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.PartyData;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyManager {

    private final PluginService pluginService;

    private final Configuration messages;
    private final Configuration command;

    private final PlayerMessage playerMethod;
    private final ServerData serverData;


    private int partyID = 1;

    public PartyManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messages = pluginService.getFiles().getMessages();
        this.command = pluginService.getFiles().getCommand();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.serverData = pluginService.getServerData();
    }

    public void createParty(Player playerLeader) {
        UserData userData = pluginService.getCache().getUserDatas().get(playerLeader.getUniqueId());

        if (userData.getPartyID() > 0){
            playerMethod.sendMessage(playerLeader, messages.getString("error.party.already-have"));
            playerMethod.sendSound(playerLeader, SoundEnum.ERROR);
            return;
        }

        userData.setPartyID(partyID);
        userData.setPlayerLeader(true);
        userData.setPlayerChannel(GroupEnum.PARTY);

        serverData.createParty(command.getInt("commands.party.config.max-players"), partyID, playerLeader.getUniqueId());
        serverData.getParty(partyID).invitePlayer(playerLeader.getUniqueId());
        partyID++;

        playerMethod.sendMessage(playerLeader, command.getString("commands.party.created"));
        playerMethod.sendSound(playerLeader, SoundEnum.ARGUMENT, "party create");
    }

    public void deleteParty(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (partyData.getPlayerLeader() != player.getUniqueId()) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-perms.delete"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        for (UUID onlineUUID : partyData.getPlayers()) {
            UserData onlineData = pluginService.getCache().getUserDatas().get(onlineUUID);
            onlineData.setPartyID(0);
            onlineData.setPlayerChannel(GroupEnum.GLOBAL);
        }

        serverData.deleteParty(userData.getPartyID());
        userData.setPlayerChannel(GroupEnum.GLOBAL);
        userData.setPartyID(0);
        playerMethod.sendMessage(player, command.getString("commands.party.deleted"));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party delete");
    }


    public void joinParty(Player player, Player leader) {

        UserData playerData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (playerData.getPartyID() > 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.already-joined"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(playerData.getPartyID());

        if (partyData.getPlayerLeader() != leader.getUniqueId()) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-leader"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        if (partyData.isFull()){
            playerMethod.sendMessage(player, messages.getString("error.party.invite.max-players")
                    .replace("%max-players%", String.valueOf(partyData.getPartySize())));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        if (partyData.isPrivate()) {
            if (!partyData.isInvited(player.getUniqueId())) {
                playerMethod.sendMessage(player, messages.getString("error.party.private"));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return;
            }
        }

        if (partyData.getInvitedPlayers().contains(player.getUniqueId())){
            partyData.removeInvitedPlayer(player.getUniqueId());
        }

        playerData.setPartyID(partyData.getChannelID());
        playerData.setPlayerChannel(GroupEnum.PARTY);
        partyData.addPlayer(player.getUniqueId());
        playerMethod.sendMessage(player, command.getString("commands.party.join"));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party join");
    }

    public void invitePlayer(Player player, Player target) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        UserData targetData = pluginService.getCache().getUserDatas().get(target.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }


        if (targetData.getPartyID() > 0) {
            playerMethod.sendMessage(player, messages.getString("error.party.target.already-have")
                                            .replace("%target%", player.getName()));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (partyData.isFull()){
            playerMethod.sendMessage(player, messages.getString("error.party.invite.max-players")
                    .replace("%max-players%", String.valueOf(partyData.getPartySize())));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        if (partyData.isInvited(target.getUniqueId())) {
            playerMethod.sendMessage(player, messages.getString("error.party.invite.already-invited")
                    .replace("%player%", player.getName()));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        partyData.invitePlayer(target.getUniqueId());
        playerMethod.sendMessage(player, command.getString("commands.party.invite.sender")
                .replace("%target%", target.getName()));
        playerMethod.sendMessage(target, command.getString("commands.party.invite.target")
                .replace("%sender%", player.getName()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party invite");
    }

    public void removeInvitePlayer(Player player, Player target) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (!partyData.isInvited(target.getUniqueId())) {
            playerMethod.sendMessage(player, messages.getString("error.party.invite.dont-invited")
                    .replace("%player%", player.getName()));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        playerMethod.sendMessage(target, command.getString("commands.party.remove-invite")
                .replace("%player%", player.getName()));
        partyData.removeInvitedPlayer(target.getUniqueId());
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party removeinvite");
    }

    public void promoteToLeader(Player player, Player target) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (!userData.isPlayerLeader()) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-perms.promote"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        if (!partyData.isInParty(target.getUniqueId())) {
            playerMethod.sendMessage(player, messages.getString("error.party.target.not-your-party")
                    .replace("%target%", target.getName()));
            return;
        }

        partyData.setPlayerLeader(target.getUniqueId());
        playerMethod.sendMessage(target, command.getString("commands.party.leader")
                .replace("%player%", player.getName()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party promote");
    }

    public void leaveParty(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (userData.isPlayerLeader()) {
            deleteParty(player);
            return;
        }

        userData.setPartyID(0);
        userData.setPlayerChannel(GroupEnum.GLOBAL);
        partyData.removePlayer(player.getUniqueId());

        if (partyData.getInvitedPlayers().contains(player.getUniqueId())){
            partyData.removeInvitedPlayer(player.getUniqueId());
        }

        playerMethod.sendMessage(player, command.getString("commands.party.left")
                .replace("%player%", player.getName()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party leave");

    }

    public void kickPlayer(Player player, Player target) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (!userData.isPlayerLeader()) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-perms.kick"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        UserData targetData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        targetData.setPartyID(0);
        targetData.setPlayerChannel(GroupEnum.GLOBAL);
        partyData.removePlayer(target.getUniqueId());

        if (partyData.getInvitedPlayers().contains(target.getUniqueId())){
            partyData.removeInvitedPlayer(target.getUniqueId());
        }

        playerMethod.sendMessage(player, command.getString("commands.party.kick.sender")
                .replace("%player%", player.getName()));
        playerMethod.sendMessage(player, command.getString("commands.party.kick.target")
                .replace("%leader%", player.getName()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "party leave");

    }

    public PartyData getPartyData(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            playerMethod.sendMessage(player, messages.getString("error.party.no-party"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return null;
        }

        return serverData.getParty(userData.getPartyID());
    }
}
