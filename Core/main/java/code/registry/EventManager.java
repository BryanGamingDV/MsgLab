package code.registry;

import code.BasicMsg;
import code.debug.DebugLogger;
import code.listeners.*;
import code.PluginService;
import code.listeners.format.ChatFormat;
import code.methods.click.ChatClickMethod;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventManager {

    private final BasicMsg plugin;
    private final PluginService pluginService;

    public EventManager(BasicMsg plugin, PluginService pluginService) {
        this.plugin = plugin;
        this.pluginService = pluginService;
    }

    public void setup() {
        DebugLogger debug = pluginService.getLogs();

        PluginManager pl = Bukkit.getServer().getPluginManager();

        pl.registerEvents(new QuitListener(pluginService), plugin);
        debug.log("QuitListener loaded!");
        pl.registerEvents(new JoinListener(pluginService), plugin);
        debug.log("JoinListener loaded!");
        pl.registerEvents(new ChatFormat(pluginService), plugin);
        debug.log("ChatFormat loaded!");
        pl.registerEvents(new ChatClickMethod(pluginService), plugin);
        debug.log("ChatClickMethod loaded!");
        pl.registerEvents(new GuiListener(pluginService), plugin);
        debug.log("GuiListener loaded!");

        plugin.getLogger().info("Events loaded!");
    }
}
