package me.bryangaming.chatlab;

import me.bryangaming.chatlab.utils.ManagingCenter;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.managers.MethodManager;
import me.bryangaming.chatlab.registry.CommandLoader;
import me.bryangaming.chatlab.registry.FileLoader;
import me.bryangaming.chatlab.registry.EventLoader;
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
    private ModuleCheck moduleCheck;

    private SupportManager supportManager;
    private CommandLoader commandLoader;
    private EventLoader eventLoader;
    private FileLoader fileLoader;

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

        fileLoader = new FileLoader(plugin, this);
        fileLoader.setup();

        supportManager = new SupportManager(this);

        moduleCreator = new ModuleCreator(this);
        moduleCheck = new ModuleCheck(this);

        managingCenter = new ManagingCenter(this);

        variables = new StringFormat(this);

        methodManager = new MethodManager(this);
        methodManager.setup();

        commandLoader = new CommandLoader(plugin, this);
        commandLoader.setup();

        eventLoader = new EventLoader(plugin, this);
        eventLoader.setup();

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
        return moduleCheck;
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

    public FileLoader getFiles() {
        return fileLoader;
    }

    public ChatLab getPlugin() {
        return plugin;
    }

    public CommandLoader getCommandRegistry() {
        return commandLoader;
    }

    public TasksManager getTasksManager() {
        return tasksManager;
    }
}