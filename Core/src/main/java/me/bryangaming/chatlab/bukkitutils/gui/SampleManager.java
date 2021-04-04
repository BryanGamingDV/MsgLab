package me.bryangaming.chatlab.bukkitutils.gui;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.bukkitutils.gui.manager.GuiSample;
import me.bryangaming.chatlab.bukkitutils.gui.sample.OnlineSample;

import java.util.HashMap;

public class SampleManager {

    private final PluginService pluginService;

    private final HashMap<String, GuiSample> classHashMap = new HashMap<>();

    public SampleManager(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        classHashMap.put("online", new OnlineSample(pluginService));
    }

    public HashMap<String, GuiSample> getClassHashMap() {
        return classHashMap;
    }
}

