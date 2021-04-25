package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.commands.*;
import me.bryangaming.chatlab.managers.group.GroupMethod;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.managers.player.PlayerStatic;
import me.bryangaming.chatlab.utils.string.StringUtils;
import me.bryangaming.chatlab.utils.string.VariableUtils;

public class MethodManager {


    private PlayerMessage playerMessage;

    private PlayerStatic playerStatic;

    private ClickChatManager chatManagent;
    private GroupMethod groupMethod;

    private HoverManager hoverManager;
    private RecipientManager recipientManager;
    private FitlerManager fitlerManager;

    private ChatManager chatManager;
    private HelpOpManager helpOpManager;
    private SocialSpyManager socialSpyManager;
    private IgnoreManager ignoreManager;
    private MsgManager msgManager;

    private PartyManager partyManager;
    private ReplyManager replyManager;
    private StaffChatManager staffChatMethod;

    private VariableUtils variableUtils;
    private StringUtils stringUtils;
    private ConditionManager conditionManager;

    private final PluginService pluginService;

    public MethodManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setup() {

        chatManagent = new ClickChatManager(pluginService);
        groupMethod = new GroupMethod(pluginService);

        recipientManager = new RecipientManager(pluginService);
        hoverManager = new HoverManager(pluginService);

        playerMessage = new PlayerMessage(pluginService);
        chatManager = new ChatManager(pluginService);
        playerStatic = new PlayerStatic(pluginService);

        fitlerManager = new FitlerManager(pluginService);
        staffChatMethod = new StaffChatManager(pluginService);
        helpOpManager = new HelpOpManager(pluginService);
        ignoreManager = new IgnoreManager(pluginService);
        socialSpyManager = new SocialSpyManager(pluginService);

        partyManager = new PartyManager(pluginService);
        replyManager = new ReplyManager(pluginService);
        msgManager = new MsgManager(pluginService);

        variableUtils = new VariableUtils(pluginService);
        stringUtils = new StringUtils(pluginService);
        conditionManager = new ConditionManager(pluginService);

        pluginService.getLogs().log("Method registered");

    }

    public PartyManager getPartyMethod() {
        return partyManager;
    }

    public GroupMethod getGroupMethod() {
        return groupMethod;
    }

    public ClickChatManager getChatManagent() {
        return chatManagent;
    }


    public HoverManager getHoverMethod() {
        return hoverManager;
    }

    public RecipientManager getRecipientMethod() {
        return recipientManager;
    }

    public PlayerMessage getSender() {
        return playerMessage;
    }

    public StaffChatManager getStaffChatMethod() {
        return staffChatMethod;
    }

    public SocialSpyManager getSocialSpyMethod() {
        return socialSpyManager;
    }

    public HelpOpManager getHelpOpMethod() {
        return helpOpManager;
    }

    public IgnoreManager getIgnoreMethod() {
        return ignoreManager;
    }

    public ReplyManager getReplyMethod() {
        return replyManager;
    }

    public MsgManager getMsgMethod() {
        return msgManager;
    }

    public ChatManager getChatMethod() {
        return chatManager;
    }

    public FitlerManager getFitlerMethod() {
        return fitlerManager;
    }

    public ConditionManager getConditionManager() {
        return conditionManager;
    }
}
