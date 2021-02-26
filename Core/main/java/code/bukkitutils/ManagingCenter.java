package code.bukkitutils;

import code.PluginService;
import code.bukkitutils.gui.manager.GuiManager;

public class ManagingCenter {


    private final PluginService pluginService;

    private SoundCreator soundCreator;
    private WorldData worldData;
    private RunnableManager runnableManager;
    private GuiManager guiManager;


    public ManagingCenter(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        soundCreator = new SoundCreator(pluginService);
        worldData = new WorldData(pluginService);
        guiManager = new GuiManager(pluginService);
        runnableManager = new RunnableManager(pluginService);

    }

    public RunnableManager getRunnableManager() {
        return runnableManager;
    }

    public SoundCreator getSoundManager() {
        return soundCreator;
    }

    public WorldData getWorldManager() {
        return worldData;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
