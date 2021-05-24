package me.bryangaming.chatlab.common.loader;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Loader;
import me.bryangaming.chatlab.common.listeners.*;
import me.bryangaming.chatlab.common.listeners.command.CommandSpyListener;
import me.bryangaming.chatlab.common.listeners.command.HelpOpListener;
import me.bryangaming.chatlab.common.listeners.command.SocialSpyListener;
import me.bryangaming.chatlab.common.listeners.text.ChatListener;
import me.bryangaming.chatlab.common.revisor.tabcomplete.TabFilter;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.events.ChatClickEvent;


import org.bukkit.plugin.PluginManager;

public class EventLoader implements Loader {

    private final PluginService pluginService;

    public EventLoader(PluginService pluginService) {
        this.pluginService = pluginService;
        load();
    }

    @Override
    public void load() {
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

        if (TextUtils.equalsIgnoreCaseOr(TextUtils.getServerVersion(Bukkit.getServer()),
                "1.13", "1.14", "1.15", "1.16")) {
            loadEvents(new TabListener(pluginService));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            TabFilter tabFilter = new TabFilter(pluginService);
        }
        pluginService.getPlugin().getLogger().info("Events loaded!");
    }

    public void loadEvents(Listener... listeners) {

        DebugLogger debug = pluginService.getLogs();
        PluginManager pl = ServerWrapper.getData().getPluginManager();

        for (Listener listener : listeners) {
            String className = listener.getClass().getName();
            debug.log(className + " loaded!");
            pl.registerEvents(listener, pluginService.getPlugin());
        }

    }

}
