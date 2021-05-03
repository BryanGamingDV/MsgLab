package me.bryangaming.chatlab.utils.module;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ModuleUtils {

    private PluginService pluginService;

    private final List<String> commandsList = new ArrayList<>();;
    private final List<String> modulesList = new ArrayList<>();

    public ModuleUtils(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        TextUtils.addToList(commandsList,
                "chat",
                "clab",
                "msg",
                "reply",
                "socialspy",
                "broadcast",
                "broadcastworld",
                "stream",
                "ignore",
                "unignore",
                "helpop",
                "staffchat",
                "channel",
                "motd",
                "commandspy",
                "announcer",
                "party");
        TextUtils.addToList(modulesList,
                "chat_format",
                "chat_revisor",
                "chat_manangent",
                "cooldown",
                "join_quit",
                "motd",
                "announcer"
        );
    }


    public boolean isPluginCommand(String command){
        return commandsList.contains(command);
    }

    public boolean isEnabledOption(ModuleType moduleType, String value){
        Configuration config = pluginService.getFiles().getConfigFile();
       return config.getStringList(moduleType.getName()).contains(value);
    }

    public List<String> getCommands() {
        return commandsList;
    }

    public List<String> getModules() {
        return modulesList;
    }
}
