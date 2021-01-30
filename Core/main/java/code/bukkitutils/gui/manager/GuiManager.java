package code.bukkitutils.gui.manager;

import code.Manager;
import code.bukkitutils.gui.SampleManager;
import code.cache.UserData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    private Manager manager;
    private final Map<String, GuiData> inventoryManager;

    private final SampleManager sampleManager;

    public GuiManager(Manager manager) {
        this.manager = manager;
        inventoryManager = new HashMap<>();
        sampleManager = new SampleManager(manager);
    }

    public void createInventory(String name, String title, int size) {
        inventoryManager.put(name, new GuiData(title, size * 9));
    }

    public void openInventory(UUID uuid, String name, int page) {

        Player player = Bukkit.getPlayer(uuid);

        // User Cache
        UserData userData = manager.getCache().getPlayerUUID().get(uuid);

        player.openInventory(sampleManager.getClassHashMap().get(name).getPage(uuid, page).build());
        userData.setGUIGroup(name);
    }

    public GuiData getInventory(String name) {
        return inventoryManager.get(name);

    }

    public SampleManager getSampleManager(){
        return sampleManager;
    }

}
