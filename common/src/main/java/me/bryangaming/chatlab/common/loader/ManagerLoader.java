package me.bryangaming.chatlab.common.loader;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Loader;
import me.bryangaming.chatlab.common.listeners.listener.ListenerManager;
import me.bryangaming.chatlab.common.managers.*;
import me.bryangaming.chatlab.common.managers.click.ClickChatManager;
import me.bryangaming.chatlab.common.managers.commands.*;
import me.bryangaming.chatlab.common.managers.group.GroupManager;
import me.bryangaming.chatlab.common.managers.sound.SoundManager;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.utils.string.VariableUtils;
import me.bryangaming.chatlab.common.utils.WorldData;

public class ManagerLoader implements Loader {


    private ListenerManager listenerManager;
    private SoundManager soundManager;
    private WorldData worldData;
    private GuiManager guiManager;
    private RunnableManager runnableManager;

    private SenderManager senderManager;

    private ClickChatManager chatManagent;
    private GroupManager groupManager;

    private HoverManager hoverManager;
    private RecipientManager recipientManager;

    private ChatManager chatManager;
    private HelpOpManager helpOpManager;
    private SocialSpyManager socialSpyManager;
    private IgnoreManager ignoreManager;
    private MsgManager msgManager;

    private PartyManager partyManager;
    private ReplyManager replyManager;
    private StaffChatManager staffChatManagerMethod;

    private VariableUtils variableUtils;
    private TextUtils textUtils;
    private ConditionManager conditionManager;

    private final PluginService pluginService;

    public ManagerLoader(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public void load() {
        listenerManager = new ListenerManager();
        soundManager = new SoundManager(pluginService);
        worldData = new WorldData(pluginService);
        guiManager = new GuiManager(pluginService);
        runnableManager = new RunnableManager(pluginService);

        chatManagent = new ClickChatManager(pluginService);
        groupManager = new GroupManager(pluginService);

        recipientManager = new RecipientManager(pluginService);
        hoverManager = new HoverManager(pluginService);

        senderManager = new SenderManager(pluginService);
        chatManager = new ChatManager(pluginService);

        staffChatManagerMethod = new StaffChatManager(pluginService);
        helpOpManager = new HelpOpManager(pluginService);
        ignoreManager = new IgnoreManager(pluginService);
        socialSpyManager = new SocialSpyManager(pluginService);

        partyManager = new PartyManager(pluginService);
        replyManager = new ReplyManager(pluginService);
        msgManager = new MsgManager(pluginService);

        variableUtils = new VariableUtils(pluginService);
        textUtils = new TextUtils(pluginService);
        conditionManager = new ConditionManager(pluginService);

        pluginService.getLogs().log("Methods registered");

    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public PartyManager getPartyMethod() {
        return partyManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
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

    public SenderManager getSender() {
        return senderManager;
    }

    public StaffChatManager getStaffChatMethod() {
        return staffChatManagerMethod;
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

    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public RunnableManager getRunnableManager() {
        return runnableManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public WorldData getWorldData(){
        return worldData;
    }
}
