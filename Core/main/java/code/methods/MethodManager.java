package code.methods;

import code.Manager;
import code.methods.chat.RadialChatMethod;
import code.methods.click.ChatMethod;
import code.methods.commands.*;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;

public class MethodManager{


    private PlayerMessage playerMessage;

    private PlayerStatic playerStatic;

    private ListenerManaging listenerManaging;
    private ChatMethod chatMethod;
    private GroupMethod groupMethod;
    private RadialChatMethod radialChatMethod;

    private HelpOpMethod helpOpMethod;
    private SocialSpyMethod socialSpyMethod;
    private IgnoreMethod ignoreMethod;
    private MsgMethod msgMethod;

    private ReplyMethod replyMethod;
    private StaffChatMethod staffChatMethod;

    private final Manager manager;

    public MethodManager(Manager manager){
        this.manager = manager;
    }

    public void setup(){

        chatMethod = new ChatMethod(manager);
        listenerManaging = new ListenerManaging(manager);
        groupMethod = new GroupMethod(manager);
        radialChatMethod = new RadialChatMethod(manager);

        playerStatic = new PlayerStatic(manager);
        playerMessage = new PlayerMessage(manager);

        staffChatMethod = new StaffChatMethod(manager);
        helpOpMethod = new HelpOpMethod(manager);
        ignoreMethod = new IgnoreMethod(manager);
        socialSpyMethod = new SocialSpyMethod(manager);

        replyMethod = new ReplyMethod(manager);
        msgMethod = new MsgMethod(manager);

        manager.getLogs().log("Method registered");

    }

    public ChatMethod getChatMethod() {
        return chatMethod;
    }
    public ListenerManaging getListenerManaging() {
        return listenerManaging;
    }
    public GroupMethod getGroupMethod() {
        return groupMethod;
    }
    public RadialChatMethod getRadialChatMethod() {
        return radialChatMethod;
    }

    public PlayerMessage getSender(){
        return playerMessage;
    }

    public StaffChatMethod getStaffChatMethod(){
        return staffChatMethod;
    }
    public SocialSpyMethod getSocialSpyMethod(){
        return socialSpyMethod;
    }
    public HelpOpMethod getHelpOpMethod(){
        return helpOpMethod;
    }
    public IgnoreMethod getIgnoreMethod(){
        return ignoreMethod;
    }
    public ReplyMethod getReplyMethod(){
        return replyMethod;
    }
    public MsgMethod getMsgMethod(){
        return msgMethod;
    }
}
