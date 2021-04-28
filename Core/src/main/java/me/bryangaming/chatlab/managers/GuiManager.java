package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.gui.SampleManager;
import me.bryangaming.chatlab.gui.manager.GuiData;
import me.bryangaming.chatlab.gui.manager.GuiSample;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    private PluginService pluginService;
    private final Map<String, GuiData> inventoryManager = new HashMap<>();

    private final SampleManager sampleManager;

    public GuiManager(PluginService pluginService) {
        this.pluginService = pluginService;
        sampleManager = new SampleManager(pluginService);
    }

    public void createInventory(String name, String title, int size) {
        inventoryManager.put(name, new GuiData(title, size * 9));
    }

    public GuiData getInventory(String name) {
        return inventoryManager.get(name);
    }

    public GuiSample getData(String name) {
        return sampleManager.getSampleMap().get(name);
    }

    public void openInventory(UUID uuid, String name, int page) {

        Player player = Bukkit.getPlayer(uuid);

        // User Cache
        UserData userData = pluginService.getCache().getUserDatas().get(uuid);

        player.openInventory(getData(name).getPage(uuid, page).build());
        userData.setGUIGroup(name);
    }


}
