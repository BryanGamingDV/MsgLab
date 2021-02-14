package code.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class UserData implements DataModel {

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

    private boolean clickMode = false;
    private final List<String> clickChat;

    public UserData(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.clickChat = new ArrayList<>();
    }


    @Override
    public Player getPlayer() {
        return player;
    }

    public boolean hasRepliedPlayer(){
        return repliedPlayer != null;
    }

    public boolean hasRepliedPlayer(UUID uuid){
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

    public boolean isCooldownCmdMode(){
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

    public boolean equalsChannelGroup(String group){
        return channelgroup.equalsIgnoreCase(group);
    }

    public boolean isGUISet(){
        return !guiGroup.equalsIgnoreCase("default");
    }

    public String getChannelGroup(){
        return channelgroup;
    }

    public boolean equalsGUIGroup(String string){
        return guiGroup.equalsIgnoreCase(string);
    }

    public int getPage(){
        return guipage;
    }

    public boolean isChangingPage(){
        return changinginv;
    }

    public void setChangeInv(Boolean status){
        changinginv = status;
    }

    public void setGUIGroup(String string){
        guiGroup = string;
    }

    public String getGUIGroup(){
        return guiGroup;
    }

    public void changePage(int page){
        guipage = page;
    }

    public void setChannelGroup(String group){
        channelgroup = group;
    }

    public List<String> getClickChat() {
        return clickChat;
    }

    public UUID getRepliedPlayer(){
        return repliedPlayer;
    }

    public void setRepliedPlayer(UUID uuid){
        repliedPlayer = uuid;
    }

    public void toggleClickMode(Boolean status) {
        clickMode = status;
    }

    public void toggleHelpOp(Boolean status){
        playerhelpopMode = status;
    }

    public void toggleStaffChat(Boolean status){
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

    public void setCooldownCmdMode(Boolean status){
        playercooldownCmdMode = status;
    }

    public void toggleSocialSpy(Boolean status) {
        socialspyMode = status;
    }

    public void resetStats(){
        if (isChangingPage()) setChangeInv(false);
        if (getPage() > 0) changePage(0);
        if (isGUISet()) setGUIGroup("default");
        if (!equalsChannelGroup("default")) setChannelGroup("default");
        if (hasRepliedPlayer()) setRepliedPlayer(null);

        if (isSocialSpyMode()) toggleSocialSpy(false);
        if (isMsgtoggleMode()) toggleMsg(false);

        if (isCooldownMode()) setCooldownMode(false);
        if (isCooldownCmdMode()) setCooldownCmdMode(false);

        if (isPlayersoundMode()) setPlayersoundMode(true);
        if (isStaffchatMode()) toggleStaffChat(false);
        if (isPlayerHelpOp()) toggleHelpOp(false);

        if (isClickMode()) toggleClickMode(false);
        if (clickChat.size() > 0) clickChat.clear();
    }

}
