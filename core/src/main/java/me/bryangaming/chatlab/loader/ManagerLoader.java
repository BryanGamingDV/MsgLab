package me.bryangaming.chatlab.loader;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Loader;
import me.bryangaming.chatlab.managers.*;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.commands.*;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.managers.sound.SoundManager;
import me.bryangaming.chatlab.utils.PlaceholderUtils;
import me.bryangaming.chatlab.utils.text.TextUtils;
import me.bryangaming.chatlab.utils.WorldData;

public class ManagerLoader implements Loader {


    private ActionManager actionManager;
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
    private IgnoreManager ignoreManager;
    private MsgManager msgManager;
    private TagsManager tagsManager;

    private PartyManager partyManager;
    private ReplyManager replyManager;
    private StaffChatManager staffChatManagerManager;

    private PlaceholderUtils placeholderUtils;
    private TextUtils textUtils;
    private ConditionManager conditionManager;

    private final PluginService pluginService;

    public ManagerLoader(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public void load() {
        soundManager = new SoundManager(pluginService);
        worldData = new WorldData(pluginService);
        guiManager = new GuiManager(pluginService);
        runnableManager = new RunnableManager(pluginService);

        groupManager = new GroupManager(pluginService);

        recipientManager = new RecipientManager(pluginService);
        hoverManager = new HoverManager(pluginService);

        senderManager = new SenderManager(pluginService);
        chatManager = new ChatManager(pluginService);

        chatManagent = new ClickChatManager(pluginService);
        actionManager = new ActionManager(pluginService);
        staffChatManagerManager = new StaffChatManager(pluginService);
        helpOpManager = new HelpOpManager(pluginService);
        ignoreManager = new IgnoreManager(pluginService);
        tagsManager = new TagsManager(pluginService);

        partyManager = new PartyManager(pluginService);
        replyManager = new ReplyManager(pluginService);
        msgManager = new MsgManager(pluginService);

        placeholderUtils = new PlaceholderUtils(pluginService);
        textUtils = new TextUtils(pluginService);
        conditionManager = new ConditionManager(pluginService);

        pluginService.getDebugger().log("Managers registered");

    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public ClickChatManager getChatManagent() {
        return chatManagent;
    }

    public TagsManager getTagsManager(){
        return tagsManager;
    }

    public HoverManager getHoverManager() {
        return hoverManager;
    }

    public RecipientManager getRecipientManager() {
        return recipientManager;
    }

    public SenderManager getSender() {
        return senderManager;
    }

    public StaffChatManager getStaffChatManager() {
        return staffChatManagerManager;
    }

    public HelpOpManager getHelpOpManager() {
        return helpOpManager;
    }

    public IgnoreManager getIgnoreManager() {
        return ignoreManager;
    }

    public ReplyManager getReplyManager() {
        return replyManager;
    }

    public MsgManager getMsgManager() {
        return msgManager;
    }

    public ChatManager getChatManager() {
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
