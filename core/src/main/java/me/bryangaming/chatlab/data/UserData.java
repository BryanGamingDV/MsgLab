package me.bryangaming.chatlab.data;

import me.bryangaming.chatlab.managers.group.GroupEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserData{

    private final UUID uuid;

    private UUID repliedPlayer = null;
    private final Player player;

    private String channelgroup = "default";

    private String guiGroup = "default";
    private int guipage = 0;

    private boolean changinginv = false;

    private boolean socialspyMode = false;
    private boolean msgtoggleMode = false;

    private boolean playercooldownMode = false;
    private boolean playercooldownCmdMode = false;

    private boolean playersoundMode = true;
    private boolean playerhelpopMode = false;
    private boolean staffchatMode = false;

    private boolean commandspyMode = false;

    private boolean clickMode = false;

    private GroupEnum playerChannel = GroupEnum.GLOBAL;

    private int partyID = 0;
    private boolean isLeader = true;

    private final List<String> clickChat = new ArrayList<>();
    private final HashMap<String, String> hashMap = new HashMap<>();

    public UserData(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
    }


    public Player getPlayer() {
        return player;
    }

    public boolean hasRepliedPlayer() {
        return repliedPlayer != null;
    }

    public boolean hasRepliedPlayer(UUID uuid) {
        return repliedPlayer == uuid;
    }

    public boolean isClickMode() {
        return clickMode;
    }

    public boolean isMsgtoggleMode() {
        return msgtoggleMode;
    }

    public boolean isPlayersoundMode() {
        return playersoundMode;
    }

    public boolean isCooldownMode() {
        return playercooldownMode;
    }

    public boolean isCooldownCmdMode() {
        return playercooldownCmdMode;
    }

    public boolean isSocialSpyMode() {
        return socialspyMode;
    }

    public boolean isPlayerHelpOp() {
        return playerhelpopMode;
    }

    public boolean isStaffchatMode() {
        return staffchatMode;
    }

    public boolean isCommandspyMode() {
        return commandspyMode;
    }

    public boolean equalsChannelGroup(String group) {
        return channelgroup.equalsIgnoreCase(group);
    }

    public boolean isGUISet() {
        return !guiGroup.equalsIgnoreCase("default");
    }

    public boolean isPlayerLeader() {
        return isLeader;
    }

    public HashMap<String, String> gethashTags() {
        return hashMap;
    }

    public GroupEnum getChannelType() {
        return playerChannel;
    }

    public int getPartyID() {
        return partyID;
    }

    public String getChannelGroup() {
        return channelgroup;
    }

    public boolean equalsGUIGroup(String string) {
        return guiGroup.equalsIgnoreCase(string);
    }

    public int getPage() {
        return guipage;
    }

    public boolean isChangingPage() {
        return changinginv;
    }

    public void setPartyID(int partyID) {
        this.partyID = partyID;
    }

    public void setChangeInv(Boolean status) {
        changinginv = status;
    }

    public void setGUIGroup(String string) {
        guiGroup = string;
    }

    public String getGUIGroup() {
        return guiGroup;
    }

    public void changePage(int page) {
        guipage = page;
    }

    public void setChannelGroup(String group) {
        channelgroup = group;
    }

    public List<String> getClickChat() {
        return clickChat;
    }

    public UUID getRepliedPlayer() {
        return repliedPlayer;
    }

    public void setRepliedPlayer(UUID uuid) {
        repliedPlayer = uuid;
    }

    public void toggleClickMode(Boolean status) {
        clickMode = status;
    }

    public void toggleHelpOp(Boolean status) {
        playerhelpopMode = status;
    }

    public void toggleStaffChat(Boolean status) {
        staffchatMode = status;
    }

    public void toggleMsg(Boolean status) {
        msgtoggleMode = status;
    }

    public void setPlayersoundMode(Boolean status) {
        playersoundMode = status;
    }

    public void setCooldownMode(Boolean status) {
        playercooldownMode = status;
    }

    public void setCooldownCmdMode(Boolean status) {
        playercooldownCmdMode = status;
    }

    public void toggleSocialSpy(Boolean status) {
        socialspyMode = status;
    }

    public void setCommandspyMode(boolean commandspyMode) {
        this.commandspyMode = commandspyMode;
    }

    public void setPlayerChannel(GroupEnum groupEnum) {
        this.playerChannel = groupEnum;
    }

    public void setPlayerLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public void resetStats() {

        if (!(gethashTags().size() > 0)) gethashTags().clear();
        if (isChangingPage()) setChangeInv(false);
        if (getPage() > 0) changePage(0);
        if (isGUISet()) setGUIGroup("default");

        if (getChannelType() != GroupEnum.GLOBAL) setPlayerChannel(GroupEnum.GLOBAL);
        if (!equalsChannelGroup("default")) setChannelGroup("default");
        if (hasRepliedPlayer()) setRepliedPlayer(null);

        if (isSocialSpyMode()) toggleSocialSpy(false);
        if (isMsgtoggleMode()) toggleMsg(false);

        if (isCooldownMode()) setCooldownMode(false);
        if (isCooldownCmdMode()) setCooldownCmdMode(false);
        if (isCommandspyMode()) setCommandspyMode(false);

        if (isPlayersoundMode()) setPlayersoundMode(true);
        if (isStaffchatMode()) toggleStaffChat(false);
        if (isPlayerHelpOp()) toggleHelpOp(false);

        if (isClickMode()) toggleClickMode(false);

        if (getPartyID() > 0) setPartyID(0);
        if (isPlayerLeader()) setPlayerLeader(false);
        if (clickChat.size() > 0) clickChat.clear();
    }

}
