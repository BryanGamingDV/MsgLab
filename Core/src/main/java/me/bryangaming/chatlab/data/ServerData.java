package me.bryangaming.chatlab.data;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerData {

    private boolean isMuted = false;

    private final List<World> worldList = new ArrayList<>();
    private final List<String> channelsMuted = new ArrayList<>();

    private final HashMap<Integer, PartyData> channelDataMap = new HashMap<>();

    private int serverTextCooldown = 0;
    private int serverCmdCooldown = 0;

    public void createParty(int maxPlayers, int id, UUID playerLeader) {
        channelDataMap.put(id, new PartyData(maxPlayers, id, playerLeader));
    }

    public void deleteParty(int id) {
        channelDataMap.remove(id);


    }

    public PartyData getParty(int id) {
        return channelDataMap.get(id);
    }

    public void muteChannel(String channel){
        channelsMuted.add(channel);
    }

    public void unmuteChannel(String channel){
        channelsMuted.remove(channel);
    }

    public boolean isChannelMuted(String channel){
        return channelsMuted.contains(channel);
    }
    public void muteWorld(World world) {
        worldList.add(world);
    }

    public void unmuteWorld(World world) {
        worldList.remove(world);
    }

    public boolean isWorldMuted(World world) {
        return worldList.contains(world);
    }

    public int getServerTextCooldown() {
        return serverTextCooldown;
    }

    public int getServerCmdCooldown() {
        return serverCmdCooldown;
    }

    public String getServerTextCooldownInString() {
        return String.valueOf(serverTextCooldown);
    }

    public String getServerCmdCooldownInString() {
        return String.valueOf(serverCmdCooldown);
    }

    public void setServerTextCooldown(int serverTextCooldown) {
        this.serverTextCooldown = serverTextCooldown;
    }

    public void setServerCmdCooldown(int serverCmdCooldown) {
        this.serverCmdCooldown = serverCmdCooldown;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }
}
