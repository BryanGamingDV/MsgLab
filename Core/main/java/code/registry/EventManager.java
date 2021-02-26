package code.registry;

import code.MsgLab;
import code.PluginService;
import code.debug.DebugLogger;
import code.listeners.GuiListener;
import code.listeners.JoinListener;
import code.listeners.QuitListener;
import code.listeners.TabListener;
import code.listeners.events.SocialSpyListener;
import code.listeners.format.ChatFormat;
import code.methods.click.ChatClickEvent;
import code.utils.StringFormat;
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
        StringFormat stringFormat = pluginService.getStringFormat();

        pl.registerEvents(new QuitListener(pluginService), plugin);
        debug.log("QuitListener loaded!");
        pl.registerEvents(new JoinListener(pluginService), plugin);
        debug.log("JoinListener loaded!");
        pl.registerEvents(new ChatFormat(pluginService), plugin);
        debug.log("ChatFormat loaded!");
        pl.registerEvents(new ChatClickEvent(pluginService), plugin);
        debug.log("ChatClickEvent loaded!");
        pl.registerEvents(new GuiListener(pluginService), plugin);
        debug.log("GuiListener loaded!");
        pl.registerEvents(new SocialSpyListener(pluginService), plugin);
        debug.log("SocialSpyListener loaded!");

        if (stringFormat.containsVersion(stringFormat.getVersion(Bukkit.getServer()), "1.12", "1.13", "1.14", "1.15", "1.16")){
            pl.registerEvents(new TabListener(pluginService), plugin);
            debug.log("TabListener loaded!");
        }
        plugin.getLogger().info("Events loaded!");
    }


}
