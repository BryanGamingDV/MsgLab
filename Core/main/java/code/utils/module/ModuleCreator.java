package code.utils.module;

import code.PluginService;

import java.util.ArrayList;
import java.util.List;

public class ModuleCreator {

    private PluginService pluginService;

    private List<String> commandsList;
    private List<String> modulesList;

    public ModuleCreator(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        commandsList = new ArrayList<>();
        modulesList = new ArrayList<>();
    }

    public List<String> getCommands() {
        return commandsList;
    }

    public List<String> getModules() {
        return modulesList;
    }
}
