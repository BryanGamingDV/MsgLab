package me.bryangaming.chatlab.spigot.impl;


import me.bryangaming.chatlab.common.wrapper.plugin.PluginBaseWrapper;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginDescriptionWrapper;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PluginWrapperImpl implements PluginBaseWrapper {

    private JavaPlugin plugin;

    public PluginWrapperImpl(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger(){
        return plugin.getLogger();
    }

    @Override
    public PluginDescriptionWrapper getDescription() {
        return new PluginDescriptionWrapperImpl(plugin.getDescription());
    }

    @Override
    public void loadMetrics(int pluginID) {
        Metrics metrics = new Metrics(plugin, 10107);
    }


}
