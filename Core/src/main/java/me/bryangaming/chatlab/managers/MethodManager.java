package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.chat.RadialChatMethod;
import me.bryangaming.chatlab.managers.click.ClickChatMethod;
import atogesputo.bryangaming.chatlab.managers.commands.*;
import me.bryangaming.chatlab.managers.commands.*;
import org.bryangaming.chatlab.managers.commands.*;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.managers.player.PlayerStatic;
import me.bryangaming.chatlab.utils.string.StringUtils;
import me.bryangaming.chatlab.utils.string.VariableUtils;

public class MethodManager {


    private PlayerMessage playerMessage;

    private PlayerStatic playerStatic;


    private ClickChatMethod chatManagent;
    private GroupMethod groupMethod;
    private RadialChatMethod radialChatMethod;

    private HoverMethod hoverMethod;
    private RecipientMethod recipientMethod;
    private FitlerMethod fitlerMethod;

    private ChatMethod chatMethod;
    private HelpOpMethod helpOpMethod;
    private SocialSpyMethod socialSpyMethod;
    private IgnoreMethod ignoreMethod;
    private MsgMethod msgMethod;

    private ReplyMethod replyMethod;
    private StaffChatMethod staffChatMethod;

    private VariableUtils variableUtils;
    private StringUtils stringUtils;

    private final PluginService pluginService;

    public MethodManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setup() {

        chatManagent = new ClickChatMethod(pluginService);
        groupMethod = new GroupMethod(pluginService);
        radialChatMethod = new RadialChatMethod(pluginService);

        recipientMethod = new RecipientMethod(pluginService);
        hoverMethod = new HoverMethod(pluginService);


        playerMessage = new PlayerMessage(pluginService);
        chatMethod = new ChatMethod(pluginService);
        playerStatic = new PlayerStatic(pluginService);

        fitlerMethod = new FitlerMethod(pluginService);
        staffChatMethod = new StaffChatMethod(pluginService);
        helpOpMethod = new HelpOpMethod(pluginService);
        ignoreMethod = new IgnoreMethod(pluginService);
        socialSpyMethod = new SocialSpyMethod(pluginService);

        replyMethod = new ReplyMethod(pluginService);
        msgMethod = new MsgMethod(pluginService);

        variableUtils = new VariableUtils(pluginService);
        stringUtils = new StringUtils(pluginService);

        pluginService.getLogs().log("Method registered");

    }

    public GroupMethod getGroupMethod() {
        return groupMethod;
    }

    public ClickChatMethod getChatManagent() {
        return chatManagent;
    }

    public RadialChatMethod getRadialChatMethod() {
        return radialChatMethod;
    }

    public HoverMethod getHoverMethod() {
        return hoverMethod;
    }

    public RecipientMethod getRecipientMethod() {
        return recipientMethod;
    }

    public PlayerMessage getSender() {
        return playerMessage;
    }

    public StaffChatMethod getStaffChatMethod() {
        return staffChatMethod;
    }

    public SocialSpyMethod getSocialSpyMethod() {
        return socialSpyMethod;
    }

    public HelpOpMethod getHelpOpMethod() {
        return helpOpMethod;
    }

    public IgnoreMethod getIgnoreMethod() {
        return ignoreMethod;
    }

    public ReplyMethod getReplyMethod() {
        return replyMethod;
    }

    public MsgMethod getMsgMethod() {
        return msgMethod;
    }

    public ChatMethod getChatMethod() {
        return chatMethod;
    }

    public FitlerMethod getFitlerMethod() {
        return fitlerMethod;
    }
}
