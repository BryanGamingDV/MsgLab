package me.bryangaming.chatlab.utils;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.GuiManager;
import me.bryangaming.chatlab.managers.RunnableManager;
import me.bryangaming.chatlab.managers.sound.SoundManager;

public class ManagingCenter {


    private final PluginService pluginService;

    private SoundManager soundManager;
    private WorldData worldData;
    private RunnableManager runnableManager;
    private GuiManager guiManager;


    public ManagingCenter(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        soundManager = new SoundManager(pluginService);
        worldData = new WorldData(pluginService);
        guiManager = new GuiManager(pluginService);
        runnableManager = new RunnableManager(pluginService);

    }

    public RunnableManager getRunnableManager() {
        return runnableManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public WorldData getWorldManager() {
        return worldData;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
