package code;

import code.bukkitutils.ManagingCenter;
import code.debug.DebugLogger;
import code.methods.MethodManager;
import code.registry.CommandRegistry;
import code.registry.ConfigManager;
import code.registry.EventManager;
import code.revisor.RevisorManager;
import code.utils.SupportManager;
import code.utils.module.ModuleCreator;
import code.utils.module.ModuleCheck;
import code.utils.StringFormat;


public class PluginService {

    private final MsgLab plugin;

    private StringFormat variables;
    private MethodManager methodManager;
    private SupportManager supportManager;

    private DebugLogger debug;

    private ModuleCreator moduleCreator;
    private ModuleCheck pathmanager;

    private CommandRegistry commandRegistry;
    private EventManager eventManager;
    private ConfigManager configManager;

    private RevisorManager revisorManager;
    private ManagingCenter managingCenter;

    private CacheManager cache;


    public PluginService(MsgLab plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup(){

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

    public RevisorManager getRevisorManager(){
        return revisorManager;
    }

    public ManagingCenter getManagingCenter() {
        return managingCenter;
    }

    public ModuleCreator getListManager(){
        return moduleCreator;
    }

    public ModuleCheck getPathManager(){
        return pathmanager;
    }

    public DebugLogger getLogs(){
        return debug;
    }

    public StringFormat getStringFormat(){
        return variables;
    }

    public CacheManager getCache(){
        return cache;
    }

    public SupportManager getSupportManager() {
        return supportManager;
    }

    public MethodManager getPlayerMethods(){
        return methodManager;
    }

    public ConfigManager getFiles(){
        return configManager;
    }

    public MsgLab getPlugin(){
        return plugin;
    }

}