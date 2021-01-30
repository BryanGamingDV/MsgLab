package code.utils.module;

import code.Manager;

import java.util.ArrayList;
import java.util.List;

public class ModuleCreator {

    private Manager manager;

    private List<String> commandsList;
    private List<String> modulesList;

    public ModuleCreator(Manager manager){
        this.manager = manager;
        setup();
    }

    public void setup(){
        commandsList = new ArrayList<>();
        modulesList = new ArrayList<>();
    }
    public List<String> getCommands(){
        return commandsList;
    }

    public List<String> getModules(){
        return modulesList;
    }
}
