package code.registry;

import code.BasicMsg;
import code.Manager;
import code.debug.DebugLogger;
import code.utils.Configuration;

import java.util.Map;

public class ConfigManager {

    // Tranquilos, esta clase no pesa mucho, pero no confunde a nadie :xd:.

    private Configuration config;
    private Configuration command;
    private Configuration players;
    private Configuration messages;
    private Configuration sounds;
    private Configuration utils;

    private final BasicMsg plugin;
    private final Manager manager;

    public ConfigManager(BasicMsg plugin, Manager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void setup() {
        config = this.setConfiguration("config.yml");
        command = this.setConfiguration("commands.yml");
        players = this.setConfiguration("players.yml");
        messages = this.setConfiguration("messages.yml");
        sounds = this.setConfiguration("sounds.yml");
        utils = this.setConfiguration("utils.yml");

        plugin.getLogger().info("Config loaded!");
    }

    public Configuration setConfiguration(String string){

        DebugLogger log = manager.getLogs();
        Map<String, Configuration> configFiles = manager.getCache().getConfigFiles();

        Configuration config = new Configuration(plugin, string);
        configFiles.put(string.split("\\.")[0], config);

        log.log(string + " loaded!");
        return config;
    }

    public Configuration getConfig() {
        return config;
    }

    public Configuration getBasicUtils(){
        return utils;
    }

    public Configuration getCommand() {
        return command;
    }

    public Configuration getPlayers() {
        return players;
    }

    public Configuration getMessages(){
        return messages;
    }

    public Configuration getSounds(){
        return sounds;
    }
}

