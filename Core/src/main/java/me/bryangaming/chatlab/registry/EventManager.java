package me.bryangaming.chatlab.registry;

import me.bryangaming.chatlab.ChatLab;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.listeners.GuiListener;
import me.bryangaming.chatlab.listeners.JoinListener;
import me.bryangaming.chatlab.listeners.QuitListener;
import me.bryangaming.chatlab.listeners.TabListener;
import me.bryangaming.chatlab.listeners.events.CommandSpyListener;
import me.bryangaming.chatlab.listeners.events.HelpOpListener;
import me.bryangaming.chatlab.listeners.events.SocialSpyListener;
import me.bryangaming.chatlab.listeners.events.server.ServerChangeListener;
import me.bryangaming.chatlab.listeners.format.ChatFormat;
import me.bryangaming.chatlab.managers.click.ChatClickEvent;
import me.bryangaming.chatlab.revisor.tabcomplete.BlockRevisor;
import me.bryangaming.chatlab.utils.StringFormat;
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
