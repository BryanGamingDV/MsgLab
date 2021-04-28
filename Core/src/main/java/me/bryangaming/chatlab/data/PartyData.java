package me.bryangaming.chatlab.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyData {

    private final int maxPlayers;

    private final int channelID;
    private UUID playerLeader;

    private boolean isPrivate;

    private final List<UUID> playerList = new ArrayList<>();
    private final List<UUID> invitedPlayers = new ArrayList<>();

    public PartyData(int maxPlayers, int channelID, UUID playerLeader) {
        this.channelID = channelID;
        this.playerLeader = playerLeader;
        this.maxPlayers = maxPlayers;
    }

    public int getChannelID() {
        return channelID;
    }

    public int getPartySize(){
        return playerList.size();
    }

    public boolean isFull() {
        return playerList.size() == maxPlayers;
    }

    public UUID getPlayerLeader() {
        return playerLeader;
    }

    public void setPlayerLeader(UUID uuid) {
        playerLeader = uuid;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate){
        this.isPrivate = isPrivate;
    }

    public void addPlayer(UUID uuid) {
        playerList.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        playerList.remove(uuid);
    }

    public List<UUID> getPlayers() {
        return playerList;
    }

    public void invitePlayer(UUID uuid) {
        invitedPlayers.add(uuid);
    }

    public void removeInvitedPlayer(UUID uuid) {
        invitedPlayers.remove(uuid);
    }

    public boolean isInvited(UUID uuid) {
        return invitedPlayers.contains(uuid);
    }

    public boolean isInParty(UUID uuid) {
        return getPlayers().contains(uuid);
    }

    public List<UUID> getInvitedPlayers() {
        return invitedPlayers;
    }
}
