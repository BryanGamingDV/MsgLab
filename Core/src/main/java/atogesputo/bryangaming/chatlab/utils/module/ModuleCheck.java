package atogesputo.bryangaming.chatlab.utils.module;


import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ModuleCheck {

    private PluginService pluginService;
    private Configuration config;

    public ModuleCheck(PluginService pluginService) {
        this.pluginService = pluginService;
        this.config = pluginService.getFiles().getConfig();
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

        Configuration utils = pluginService.getFiles().getBasicUtils();

        for (String disabledCmds : utils.getStringList("fitlers.cmd.disabled-cmds")) {
            if (disabledCmds.equalsIgnoreCase(StringUtils.remove(message, "/"))) {
                return true;
            }
        }

        return false;
    }

    public boolean isCommandEnabled(String optionName) {
        List<String> commandFile = config.getStringList("config.modules.enabled-commands");
        for (String command : commandFile) {
            if (command.equalsIgnoreCase(optionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOptionEnabled(String optionName) {
        List<String> optionFile = config.getStringList("config.modules.enabled-options");
        for (String optionEnabledOptions : optionFile) {
            if (optionEnabledOptions.equalsIgnoreCase(optionName)) {
                return true;
            }
        }
        return false;
    }

    public String getUsage(String command, String... args) {

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            if (!(arg.contains(","))) {
                message.append(arg).append(" ");
                continue;
            }

            message.append("[").append(arg).append("] ");
        }

        return "/" + command + " " + message.toString();

    }
}