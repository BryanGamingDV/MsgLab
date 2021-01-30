package code.bukkitutils;

import code.PluginService;
import code.bukkitutils.gui.manager.GuiManager;

public class ManagingCenter {


    private final PluginService pluginService;

    private SoundManager soundManager;
    private WorldManager worldManager;
    private RunnableManager runnableManager;
    private GuiManager guiManager;


    public ManagingCenter(PluginService pluginService){
        this.pluginService = pluginService;
        setup();
    }

    public void setup(){
        soundManager = new SoundManager(pluginService);
        worldManager = new WorldManager(pluginService);
        guiManager = new GuiManager(pluginService);
        runnableManager = new RunnableManager(pluginService);

    }

    public RunnableManager getRunnableManager() {
        return runnableManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
