package code.methods;

import code.PluginService;
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

    private final PluginService pluginService;

    public MethodManager(PluginService pluginService){
        this.pluginService = pluginService;
    }

    public void setup(){

        chatMethod = new ChatMethod(pluginService);
        listenerManaging = new ListenerManaging(pluginService);
        groupMethod = new GroupMethod(pluginService);
        radialChatMethod = new RadialChatMethod(pluginService);

        playerStatic = new PlayerStatic(pluginService);
        playerMessage = new PlayerMessage(pluginService);

        staffChatMethod = new StaffChatMethod(pluginService);
        helpOpMethod = new HelpOpMethod(pluginService);
        ignoreMethod = new IgnoreMethod(pluginService);
        socialSpyMethod = new SocialSpyMethod(pluginService);

        replyMethod = new ReplyMethod(pluginService);
        msgMethod = new MsgMethod(pluginService);

        pluginService.getLogs().log("Method registered");

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
