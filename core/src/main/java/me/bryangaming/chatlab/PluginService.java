package me.bryangaming.chatlab;

import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.loader.EventLoader;
import me.bryangaming.chatlab.loader.FileLoader;
import me.bryangaming.chatlab.loader.ManagerLoader;
import me.bryangaming.chatlab.loader.command.CommandLoader;
import me.bryangaming.chatlab.managers.SupportManager;
import me.bryangaming.chatlab.redis.RedisConnection;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.tasks.TasksManager;

public class PluginService {

    private final ChatLab plugin;

    private ServerData serverData;

    private RedisConnection redisConnection;

    private ManagerLoader managerLoader;
    private DebugLogger debug;

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

    private void setup(){
        serverData = new ServerData();
        debug = new DebugLogger(plugin);
        cache = new CacheManager(this);
        fileLoader = new FileLoader(this);
        supportManager = new SupportManager(this);
        managerLoader = new ManagerLoader(this);
        managerLoader.load();
        commandLoader = new CommandLoader(this);
        eventLoader = new EventLoader(this);
        cooldownData = new CooldownData(this);
        tasksManager = new TasksManager(this);
        loadRedis();
    }

    private void loadRedis(){ ;
        if (!fileLoader.getConfigFile().getBoolean("options.redis.enabled")){
            return;
        }

        debug.log("Loading redis.");
        redisConnection = new RedisConnection(this,
                fileLoader.getConfigFile().getString("options.redis.password"),
                fileLoader.getConfigFile().getString("options.redis.hostname"),
                fileLoader.getConfigFile().getInt("options.redis.password"));
        redisConnection.redisConnect();
        redisConnection.subscribeChannel("chatlab");
        debug.log("Redis loaded!");
    }

    public RedisConnection getRedisConnection(){
        return redisConnection;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public CooldownData getCooldownData() {
        return cooldownData;
    }

    public DebugLogger getDebugger() {
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

    public EventLoader getEventLoader(){
        return eventLoader;
    }

    public TasksManager getTasksManager() {
        return tasksManager;
    }
}