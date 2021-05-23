package me.bryangaming.chatlab.common;

import me.bryangaming.chatlab.common.loader.CommandLoader;
import me.bryangaming.chatlab.common.loader.EventLoader;
import me.bryangaming.chatlab.common.loader.FileLoader;
import me.bryangaming.chatlab.common.loader.ManagerLoader;
import me.bryangaming.chatlab.common.revisor.CooldownData;
import me.bryangaming.chatlab.common.utils.SupportManager;
import me.bryangaming.chatlab.common.utils.module.ModuleUtils;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.tasks.TasksManager;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginBaseWrapper;


public class PluginService {

    private final PluginBaseWrapper plugin;

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

    public PluginService(PluginBaseWrapper plugin) {
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

    public PluginBaseWrapper getPlugin() {
        return plugin;
    }

    public CommandLoader getCommandLoader() {
        return commandLoader;
    }

    public TasksManager getTasksManager() {
        return tasksManager;
    }
}