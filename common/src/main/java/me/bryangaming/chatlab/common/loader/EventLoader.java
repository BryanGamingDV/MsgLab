package me.bryangaming.chatlab.common.loader;

import me.bryangaming.chatlab.api.Event;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.api.Loader;
import me.bryangaming.chatlab.common.builders.ListenerBuilder;
import me.bryangaming.chatlab.common.listeners.*;
import me.bryangaming.chatlab.common.listeners.command.CommandSpyListener;
import me.bryangaming.chatlab.common.listeners.command.HelpOpListener;
import me.bryangaming.chatlab.common.listeners.command.SocialSpyListener;
import me.bryangaming.chatlab.common.listeners.listener.ListenerManager;
import me.bryangaming.chatlab.common.listeners.text.ChatListener;
import me.bryangaming.chatlab.common.revisor.tabcomplete.TabFilter;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.debug.DebugLogger;
import me.bryangaming.chatlab.common.events.ChatClickEvent;


import org.bukkit.plugin.PluginManager;

public class EventLoader implements Loader {

    private final PluginService pluginService;

    private final ListenerManager listenerManager;

    public EventLoader(PluginService pluginService) {
        this.pluginService = pluginService;
        this.listenerManager = pluginService.getPlayerManager().getListenerManager();
        load();
    }

    @Override
    public void load() {
        listenerManager.registerListeners(
                new SocialSpyListener(pluginService),
                new CommandSpyListener(pluginService),
                new HelpOpListener(pluginService),
                new RevisorListener(pluginService));

        if (TextUtils.equalsIgnoreCaseOr(TextUtils.getServerVersion(Bukkit.getServer()),
                "1.13", "1.14", "1.15", "1.16")) {
            loadEvents(new TabListener(pluginService));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            TabFilter tabFilter = new TabFilter(pluginService);
        }
        pluginService.getPlugin().getLogger().info("Events loaded!");
    }

    public void callEvent(Event event){
        listenerManager.callEvent(event);
    }

}
