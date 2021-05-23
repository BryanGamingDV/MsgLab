package me.bryangaming.chatlab.common.managers;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.gui.GuiData;
import me.bryangaming.chatlab.common.gui.GuiSample;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.SampleManager;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;

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

        PlayerWrapper player = ServerWrapper.getData().getPlayer(uuid);

        // User Cache
        UserData userData = pluginService.getCache().getUserDatas().get(uuid);

        player.openInventory(getData(name).getPage(uuid, page).build());
        userData.setGUIGroup(name);
    }


}
