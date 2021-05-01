package me.bryangaming.chatlab.loader;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Loader;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.utils.Configuration;

import java.util.Map;

public class FileLoader implements Loader{


    private Configuration configFile;
    private Configuration commandFile;
    private Configuration playersFile;
    private Configuration messagesFile;
    private Configuration soundsFile;
    private Configuration formatsFile;

    private final PluginService pluginService;

    public FileLoader(PluginService pluginService) {
        this.pluginService = pluginService;
        load();
    }

    @Override
    public void load() {
        configFile = this.setConfiguration("config.yml");
        commandFile = this.setConfiguration("commands.yml");
        playersFile = this.setConfiguration("players.yml");
        messagesFile = this.setConfiguration("messages.yml");
        soundsFile = this.setConfiguration("sounds.yml");
        formatsFile = this.setConfiguration("formats.yml");

        pluginService.getPlugin().getLogger().info("Config loaded!");
    }

    private Configuration setConfiguration(String string) {

        DebugLogger log = pluginService.getLogs();
        Map<String, Configuration> configFiles = pluginService.getCache().getConfigFiles();

        Configuration config = new Configuration(pluginService.getPlugin(), string);
        configFiles.put(string.split("\\.")[0], config);

        log.log(string + " loaded!");
        return config;
    }

    public Configuration getConfigFile() {
        return configFile;
    }

    public Configuration getFormatsFile() {
        return formatsFile;
    }

    public Configuration getCommandFile() {
        return commandFile;
    }

    public Configuration getPlayersFile() {
        return playersFile;
    }

    public Configuration getMessagesFile() {
        return messagesFile;
    }

    public Configuration getSoundsFile() {
        return soundsFile;
    }
}

