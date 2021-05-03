package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.PartyData;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyManager {

    private final PluginService pluginService;

    private final Configuration messagesFile;
    private final Configuration commandFile;

    private final SenderManager senderManager;
    private final ServerData serverData;

    private int partyID = 1;

    public PartyManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messagesFile = pluginService.getFiles().getMessagesFile();
        this.commandFile = pluginService.getFiles().getCommandFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.serverData = pluginService.getServerData();
    }

    public void createParty(Player playerLeader) {
        UserData userData = pluginService.getCache().getUserDatas().get(playerLeader.getUniqueId());

        if (userData.getPartyID() > 0){
            senderManager.sendMessage(playerLeader, messagesFile.getString("error.party.already-have"));
            senderManager.playSound(playerLeader, SoundEnum.ERROR);
            return;
        }

        userData.setPartyID(partyID);
        userData.setPlayerLeader(true);
        userData.setPlayerChannel(GroupEnum.PARTY);

        serverData.createParty(commandFile.getInt("commands.party.config.max-players"), partyID, playerLeader.getUniqueId());
        serverData.getParty(partyID).invitePlayer(playerLeader.getUniqueId());
        partyID++;

        senderManager.sendMessage(playerLeader, commandFile.getString("commands.party.created"));
        senderManager.playSound(playerLeader, SoundEnum.ARGUMENT, "party create");
    }

    public void deleteParty(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (partyData.getPlayerLeader() != player.getUniqueId()) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-perms.delete"));
            senderManager.playSound(player, SoundEnum.ERROR);
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
        senderManager.sendMessage(player, commandFile.getString("commands.party.deleted"));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party delete");
    }


    public void joinParty(Player player, Player leader) {

        UserData playerData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (playerData.getPartyID() > 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.already-joined"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(playerData.getPartyID());

        if (partyData.getPlayerLeader() != leader.getUniqueId()) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-leader"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        if (partyData.isFull()){
            senderManager.sendMessage(player, messagesFile.getString("error.party.invite.max-players")
                    .replace("%max-players%", String.valueOf(partyData.getPartySize())));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        if (partyData.isPrivate()) {
            if (!partyData.isInvited(player.getUniqueId())) {
                senderManager.sendMessage(player, messagesFile.getString("error.party.private"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return;
            }
        }

        if (partyData.getInvitedPlayers().contains(player.getUniqueId())){
            partyData.removeInvitedPlayer(player.getUniqueId());
        }

        playerData.setPartyID(partyData.getChannelID());
        playerData.setPlayerChannel(GroupEnum.PARTY);
        partyData.addPlayer(player.getUniqueId());
        senderManager.sendMessage(player, commandFile.getString("commands.party.join"));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party join");
    }

    public void invitePlayer(Player player, Player target) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        UserData targetData = pluginService.getCache().getUserDatas().get(target.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }


        if (targetData.getPartyID() > 0) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.target.already-have")
                                            .replace("%target%", player.getName()));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (partyData.isFull()){
            senderManager.sendMessage(player, messagesFile.getString("error.party.invite.max-players")
                    .replace("%max-players%", String.valueOf(partyData.getPartySize())));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        if (partyData.isInvited(target.getUniqueId())) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.invite.already-invited")
                    .replace("%player%", player.getName()));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        partyData.invitePlayer(target.getUniqueId());
        senderManager.sendMessage(player, commandFile.getString("commands.party.invite.sender")
                .replace("%target%", target.getName()));
        senderManager.sendMessage(target, commandFile.getString("commands.party.invite.target")
                .replace("%sender%", player.getName()));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party invite");
    }

    public void removeInvitePlayer(Player player, Player target) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (!partyData.isInvited(target.getUniqueId())) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.invite.dont-invited")
                    .replace("%player%", player.getName()));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        senderManager.sendMessage(target, commandFile.getString("commands.party.remove-invite")
                .replace("%player%", player.getName()));
        partyData.removeInvitedPlayer(target.getUniqueId());
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party removeinvite");
    }

    public void promoteToLeader(Player player, Player target) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (!userData.isPlayerLeader()) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-perms.promote"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        if (!partyData.isInParty(target.getUniqueId())) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.target.not-your-party")
                    .replace("%target%", target.getName()));
            return;
        }

        partyData.setPlayerLeader(target.getUniqueId());
        senderManager.sendMessage(target, commandFile.getString("commands.party.leader")
                .replace("%player%", player.getName()));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party promote");
    }

    public void leaveParty(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
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

        senderManager.sendMessage(player, commandFile.getString("commands.party.left")
                .replace("%player%", player.getName()));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party leave");

    }

    public void kickPlayer(Player player, Player target) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        PartyData partyData = serverData.getParty(userData.getPartyID());

        if (!userData.isPlayerLeader()) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-perms.kick"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return;
        }

        if (!partyData.isInParty(target.getUniqueId())) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.target.not-your-party")
                    .replace("%target%", target.getName()));
            return;
        }

        UserData targetData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        targetData.setPartyID(0);
        targetData.setPlayerChannel(GroupEnum.GLOBAL);
        partyData.removePlayer(target.getUniqueId());

        if (partyData.getInvitedPlayers().contains(target.getUniqueId())){
            partyData.removeInvitedPlayer(target.getUniqueId());
        }

        senderManager.sendMessage(player, commandFile.getString("commands.party.kick.sender")
                .replace("%player%", player.getName()));
        senderManager.sendMessage(player, commandFile.getString("commands.party.kick.target")
                .replace("%leader%", player.getName()));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "party leave");

    }

    public PartyData getPartyData(Player player) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(player, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return null;
        }

        return serverData.getParty(userData.getPartyID());
    }
}
