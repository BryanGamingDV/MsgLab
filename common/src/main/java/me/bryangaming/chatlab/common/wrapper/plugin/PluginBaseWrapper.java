package me.bryangaming.chatlab.common.wrapper.plugin;

import java.io.File;
import java.util.logging.Logger;

public interface PluginBaseWrapper{

    File getDataFolder();

    Logger getLogger();

    PluginDescriptionWrapper getDescription();

    void loadMetrics(int pluginID);

}
