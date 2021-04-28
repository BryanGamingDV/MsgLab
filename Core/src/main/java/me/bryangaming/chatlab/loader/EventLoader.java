package me.bryangaming.chatlab.loader;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.debug.DebugLogger;
import me.bryangaming.chatlab.events.ChatClickEvent;
import me.bryangaming.chatlab.listeners.*;
import me.bryangaming.chatlab.listeners.command.CommandSpyListener;
import me.bryangaming.chatlab.listeners.command.HelpOpListener;
import me.bryangaming.chatlab.listeners.command.SocialSpyListener;
import me.bryangaming.chatlab.listeners.text.ChatListener;
import me.bryangaming.chatlab.revisor.tabcomplete.TabFitler;
import me.bryangaming.chatlab.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EventLoader {

    private final PluginService pluginService;

    public EventLoader(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setup() {

        StringFormat stringFormat = pluginService.getStringFormat();

        loadEvents(
                new JoinListener(pluginService),
                new QuitListener(pluginService),
                new SendTextListener(pluginService),
                new ChatClickEvent(pluginService),
                new GuiListener(pluginService),
                new HelpOpListener(pluginService),
                new SocialSpyListener(pluginService),
                new CommandSpyListener(pluginService),
                new ServerChangeListener(pluginService),
                new RevisorListener(pluginService),
                new ChatListener(pluginService));

        if (stringFormat.containsVersion(stringFormat.getVersion(Bukkit.getServer()), stringFormat.getLegacyVersion())) {
            loadEvents(new TabListener(pluginService));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            TabFitler tabFitler = new TabFitler(pluginService);
        }
        pluginService.getPlugin().getLogger().info("Events loaded!");
    }

    public void loadEvents(Listener... listeners) {

        DebugLogger debug = pluginService.getLogs();
        PluginManager pl = Bukkit.getServer().getPluginManager();

        for (Listener listener : listeners) {
            String className = listener.getClass().getName();
            debug.log(className + " loaded!");
            pl.registerEvents(listener, pluginService.getPlugin());
        }

    }

}
