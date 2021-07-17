package me.bryangaming.chatlab.commands.translation;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.PlaceholderUtils;
import me.bryangaming.chatlab.utils.text.TextUtils;
import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.BukkitDefaultTranslationProvider;
import me.fixeddev.commandflow.translator.TranslationProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.swing.plaf.TextUI;
import java.util.HashMap;
import java.util.Map;

public class CommandTranslation implements TranslationProvider {

    private final PluginService pluginService;

    protected Map<String, String> translations;

    private final Configuration messagesFile;

    public CommandTranslation(PluginService pluginService) {
        this.pluginService = pluginService;
        this.messagesFile = pluginService.getFiles().getMessagesFile();
        translations = new HashMap<>();
        setup();
    }

    public void setup() {
        translations.put("command.subcommand.invalid", "1. The subcommand %s doesn't exist!");
        translations.put("command.no-permission", "2. No permission.");
        translations.put("argument.no-more", "3. No more arguments were found, size: %s position: %s");
        translations.put("sender.unknown", "5. Unknown command sender!");
        pluginService.getDebugger().log("Translator created!");
    }


    @Override
    public String getTranslation(Namespace namespace, String key) {
        switch (key){
            case "sender.only-player":
                return messagesFile.getString("global-errors.console");
            case "player.offline":
                return TextUtils.convertBasicString(messagesFile.getString("global-errors.player-offline"));
        }

        return translations.get(key);
    }
}