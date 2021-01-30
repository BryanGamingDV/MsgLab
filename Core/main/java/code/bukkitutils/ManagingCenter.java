package code.bukkitutils;

import code.Manager;
import code.bukkitutils.gui.manager.GuiManager;

public class ManagingCenter {


    private final Manager manager;

    private SoundManager soundManager;
    private WorldManager worldManager;
    private RunnableManager runnableManager;
    private GuiManager guiManager;


    public ManagingCenter(Manager manager){
        this.manager = manager;
        setup();
    }

    public void setup(){
        soundManager = new SoundManager(manager);
        worldManager = new WorldManager(manager);
        guiManager = new GuiManager(manager);
        runnableManager = new RunnableManager(manager);

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
