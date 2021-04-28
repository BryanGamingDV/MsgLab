package me.bryangaming.chatlab.utils.module;


import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import org.apache.commons.lang.StringUtils;

public class ModuleCheck {

    private PluginService pluginService;

    public ModuleCheck(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public boolean isPluginCommand(String commandName) {
        boolean clabCommand = false;

        for (String allCommands : pluginService.getListManager().getCommands()) {
            if (allCommands.equalsIgnoreCase(StringUtils.remove(commandName, "/"))) {
                clabCommand = true;
                break;
            }
        }

        return clabCommand;
    }

    public boolean isCommandDisabledInCooldown(String message) {

        Configuration utils = pluginService.getFiles().getFormatsFile();

        for (String disabledCmds : utils.getStringList("filters.cmd.disabled-cmds")) {
            if (disabledCmds.equalsIgnoreCase(StringUtils.remove(message, "/"))) {
                return true;
            }
        }

        return false;
    }


}