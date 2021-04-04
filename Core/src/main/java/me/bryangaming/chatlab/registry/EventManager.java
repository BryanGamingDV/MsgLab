package atogesputo.bryangaming.chatlab.registry;

import atogesputo.bryangaming.chatlab.ChatLab;
import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.debug.DebugLogger;
import atogesputo.bryangaming.chatlab.utils.StringFormat;
import atogesputo.bryangaming.chatlab.listeners.GuiListener;
import atogesputo.bryangaming.chatlab.listeners.JoinListener;
import atogesputo.bryangaming.chatlab.listeners.QuitListener;
import atogesputo.bryangaming.chatlab.listeners.TabListener;
import atogesputo.bryangaming.chatlab.listeners.events.CommandSpyListener;
import atogesputo.bryangaming.chatlab.listeners.events.HelpOpListener;
import atogesputo.bryangaming.chatlab.listeners.events.SocialSpyListener;
import atogesputo.bryangaming.chatlab.listeners.events.server.ServerChangeListener;
import atogesputo.bryangaming.chatlab.listeners.format.ChatFormat;
import atogesputo.bryangaming.chatlab.managers.click.ChatClickEvent;
import atogesputo.bryangaming.chatlab.revisor.tabcomplete.BlockRevisor;
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
