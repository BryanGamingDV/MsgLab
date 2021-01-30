package code.bukkitutils.gui.manager;

import code.PluginService;
import code.bukkitutils.gui.SampleManager;
import code.data.UserData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    private PluginService pluginService;
    private final Map<String, GuiData> inventoryManager;

    private final SampleManager sampleManager;

    public GuiManager(PluginService pluginService) {
        this.pluginService = pluginService;
        inventoryManager = new HashMap<>();
        sampleManager = new SampleManager(pluginService);
    }

    public void createInventory(String name, String title, int size) {
        inventoryManager.put(name, new GuiData(title, size * 9));
    }

    public void openInventory(UUID uuid, String name, int page) {

        Player player = Bukkit.getPlayer(uuid);

        // User Cache
        UserData userData = pluginService.getCache().getPlayerUUID().get(uuid);

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
