package code.registry;

import code.ChatLab;
import code.PluginService;
import code.debug.DebugLogger;
import code.listeners.GuiListener;
import code.listeners.JoinListener;
import code.listeners.QuitListener;
import code.listeners.TabListener;
import code.listeners.events.CommandSpyListener;
import code.listeners.events.HelpOpListener;
import code.listeners.events.SocialSpyListener;
import code.listeners.events.server.ServerChangeListener;
import code.listeners.format.ChatFormat;
import code.managers.click.ChatClickEvent;
import code.revisor.tabcomplete.BlockRevisor;
import code.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EventManager {

    private final ChatLab plugin;
    private final PluginService pluginService;

    public EventManager(ChatLab plugin, PluginService pluginService) {
        this.plugin = plugin;
        this.pluginService = pluginService;
    }

    public void setup() {

        StringFormat stringFormat = pluginService.getStringFormat();

        loadEvents(
                new QuitListener(pluginService),
                new JoinListener(pluginService),
                new ChatFormat(pluginService),
                new ChatClickEvent(pluginService),
                new GuiListener(pluginService),
                new HelpOpListener(pluginService),
                new SocialSpyListener(pluginService),
                new CommandSpyListener(pluginService),
                new ServerChangeListener(pluginService));

        if (stringFormat.containsVersion(stringFormat.getVersion(Bukkit.getServer()), stringFormat.getLegacyVersion())){
            loadEvents(new TabListener(pluginService));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            BlockRevisor blockRevisor = new BlockRevisor(pluginService);
        }
        plugin.getLogger().info("Events loaded!");
    }

    public void loadEvents(Listener... listeners){

        DebugLogger debug = pluginService.getLogs();
        PluginManager pl = Bukkit.getServer().getPluginManager();

        for (Listener listener : listeners){
            String className = listener.getClass().getName();
            debug.log(className + " loaded!");
            pl.registerEvents(listener, plugin);
        }

    }

}
