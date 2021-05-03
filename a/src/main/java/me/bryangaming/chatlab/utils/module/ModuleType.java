package me.bryangaming.chatlab.utils.module;

public enum ModuleType {

    COMMAND("modules.enabled-commands"), MODULE("modules.enabled-options");

    private final String moduleName;

    ModuleType(String moduleName){
        this.moduleName = moduleName;
    }

    public String getName(){
        return moduleName;
    }
}
