package me.bryangaming.chatlab.commands.translation;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.bukkit.BukkitDefaultTranslationProvider;

import java.util.HashMap;
import java.util.Map;

public class CommandTranslation extends BukkitDefaultTranslationProvider {

    private final PluginService pluginService;

    protected Map<String, String> translations;

    private final Configuration messages;

    public CommandTranslation(PluginService pluginService) {
        this.pluginService = pluginService;
        this.messages = pluginService.getFiles().getMessagesFile();
        translations = new HashMap<>();
        setup();
    }

    public void setup() {
        translations.put("command.subcommand.invalid", "1. The subcommand %s doesn't exist!");
        translations.put("command.no-permission", "2. No permission.");
        translations.put("argument.no-more", "3. No more arguments were found, size: %s position: %s");
        translations.put("player.offline", "4. The player %s is offline!");
        translations.put("sender.unknown", "5. Unknown command sender!");
        translations.put("sender.only-player", messages.getString("error.console"));
        pluginService.getLogs().log("Translator created!");
    }

    public String getTranslation(String key) {
        return translations.get(key);
    }

    @Override
    public String getTranslation(Namespace namespace, String key) {
        return getTranslation(key);
    }
}