package me.bryangaming.chatlab;

import me.bryangaming.chatlab.bukkitutils.ManagingCenter;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.managers.MethodManager;
import me.bryangaming.chatlab.registry.CommandRegistry;
import me.bryangaming.chatlab.registry.ConfigManager;
import me.bryangaming.chatlab.registry.EventManager;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.tasks.TasksManager;
import me.bryangaming.chatlab.utils.StringFormat;
import me.bryangaming.chatlab.utils.SupportManager;
import me.bryangaming.chatlab.utils.module.ModuleCheck;
import me.bryangaming.chatlab.utils.module.ModuleCreator;


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

    private CooldownData cooldownData;
    private ManagingCenter managingCenter;

    private TasksManager tasksManager;
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

        cooldownData = new CooldownData(this);
        tasksManager = new TasksManager(this);
    }

    public ServerData getServerData() {
        return serverData;
    }

    public CooldownData getCooldownData() {
        return cooldownData;
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

    public TasksManager getTasksManager() {
        return tasksManager;
    }
}