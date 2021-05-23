package me.bryangaming.chatlab.spigot.impl;

import me.bryangaming.chatlab.common.wrapper.plugin.PluginDescriptionWrapper;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginDescriptionWrapperImpl implements PluginDescriptionWrapper {

    private PluginDescriptionFile pluginDescriptionFile;

    public PluginDescriptionWrapperImpl(PluginDescriptionFile pluginDescriptionFile){
        this.pluginDescriptionFile = pluginDescriptionFile;
    }
    @Override
    public String getAuthor() {
        return pluginDescriptionFile.getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return pluginDescriptionFile.getVersion();
    }
}
