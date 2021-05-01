package me.bryangaming.chatlab;

import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.loader.CommandLoader;
import me.bryangaming.chatlab.loader.EventLoader;
import me.bryangaming.chatlab.loader.FileLoader;
import me.bryangaming.chatlab.loader.ManagerLoader;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.tasks.TasksManager;
import me.bryangaming.chatlab.utils.SupportManager;
import me.bryangaming.chatlab.utils.module.ModuleUtils;


public class PluginService {

    private final ChatLab plugin;

    private ServerData serverData;

    private ManagerLoader managerLoader;
    private DebugLogger debug;

    private ModuleUtils moduleUtils;

    private SupportManager supportManager;
    private CommandLoader commandLoader;
    private EventLoader eventLoader;
    private FileLoader fileLoader;

    private CooldownData cooldownData;

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
        fileLoader = new FileLoader(this);
        moduleUtils = new ModuleUtils(this);
        supportManager = new SupportManager(this);
        managerLoader = new ManagerLoader(this);
        managerLoader.load();
        commandLoader = new CommandLoader(this);
        eventLoader = new EventLoader(this);
        cooldownData = new CooldownData(this);
        tasksManager = new TasksManager(this);
    }

    public ServerData getServerData() {
        return serverData;
    }

    public CooldownData getCooldownData() {
        return cooldownData;
    }


    public ModuleUtils getListManager() {
        return moduleUtils;
    }

    public DebugLogger getLogs() {
        return debug;
    }

    public CacheManager getCache() {
        return cache;
    }

    public SupportManager getSupportManager() {
        return supportManager;
    }

    public ManagerLoader getPlayerManager() {
        return managerLoader;
    }

    public FileLoader getFiles() {
        return fileLoader;
    }

    public ChatLab getPlugin() {
        return plugin;
    }

    public CommandLoader getCommandLoader() {
        return commandLoader;
    }

    public TasksManager getTasksManager() {
        return tasksManager;
    }
}