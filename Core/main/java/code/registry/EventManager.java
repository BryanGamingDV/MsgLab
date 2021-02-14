package code.registry;

import code.MsgLab;
import code.debug.DebugLogger;
import code.listeners.*;
import code.PluginService;
import code.listeners.events.SocialSpyListener;
import code.listeners.format.ChatFormat;
import code.methods.click.ChatClickMethod;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventManager {

    private final MsgLab plugin;
    private final PluginService pluginService;

    public EventManager(MsgLab plugin, PluginService pluginService) {
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
        pl.registerEvents(new SocialSpyListener(pluginService), plugin);
        debug.log("SocialSpyListener loaded!");

        plugin.getLogger().info("Events loaded!");
    }
}
