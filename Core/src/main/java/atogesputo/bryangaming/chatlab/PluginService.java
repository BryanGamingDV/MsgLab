package atogesputo.bryangaming.chatlab;

import atogesputo.bryangaming.chatlab.bukkitutils.ManagingCenter;
import atogesputo.bryangaming.chatlab.data.ServerData;
import atogesputo.bryangaming.chatlab.debug.DebugLogger;
import atogesputo.bryangaming.chatlab.managers.MethodManager;
import atogesputo.bryangaming.chatlab.registry.CommandRegistry;
import atogesputo.bryangaming.chatlab.registry.ConfigManager;
import atogesputo.bryangaming.chatlab.registry.EventManager;
import atogesputo.bryangaming.chatlab.revisor.RevisorManager;
import atogesputo.bryangaming.chatlab.utils.StringFormat;
import atogesputo.bryangaming.chatlab.utils.SupportManager;
import atogesputo.bryangaming.chatlab.utils.module.ModuleCheck;
import atogesputo.bryangaming.chatlab.utils.module.ModuleCreator;


public class PluginService {

    private final ChatLab plugin;

    private ServerData serverData;

    private StringFormat variables;
    private MethodManager methodManager;
    private DebugLogger debug;

    private ModuleCreator moduleCreator;
    private ModuleCheck pathmanager;

    private SupportManager supportManager;
    private CommandRegistry commandRegistry;
    private EventManager eventManager;
    private ConfigManager configManager;

    private RevisorManager revisorManager;
    private ManagingCenter managingCenter;

    private CacheManager cache;


    public PluginService(ChatLab plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {

        serverData = new ServerData();

        debug = new DebugLogger(plugin);

        cache = new CacheManager(this);

        configManager = new ConfigManager(plugin, this);
        configManager.setup();

        supportManager = new SupportManager(this);

        moduleCreator = new ModuleCreator(this);

        pathmanager = new ModuleCheck(this);

        managingCenter = new ManagingCenter(this);

        variables = new StringFormat(configManager, this);

        methodManager = new MethodManager(this);
        methodManager.setup();

        commandRegistry = new CommandRegistry(plugin, this);
        commandRegistry.setup();

        eventManager = new EventManager(plugin, this);
        eventManager.setup();

        revisorManager = new RevisorManager(this);

    }

    public ServerData getServerData() {
        return serverData;
    }

    public RevisorManager getRevisorManager() {
        return revisorManager;
    }

    public ManagingCenter getManagingCenter() {
        return managingCenter;
    }

    public ModuleCreator getListManager() {
        return moduleCreator;
    }

    public ModuleCheck getPathManager() {
        return pathmanager;
    }

    public DebugLogger getLogs() {
        return debug;
    }

    public StringFormat getStringFormat() {
        return variables;
    }

    public CacheManager getCache() {
        return cache;
    }

    public SupportManager getSupportManager() {
        return supportManager;
    }

    public MethodManager getPlayerMethods() {
        return methodManager;
    }

    public ConfigManager getFiles() {
        return configManager;
    }

    public ChatLab getPlugin() {
        return plugin;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }
}