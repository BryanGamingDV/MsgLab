package me.bryangaming.chatlab.gui;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.gui.manager.GuiSample;
import me.bryangaming.chatlab.gui.sample.OnlineSample;

import java.util.HashMap;

public class SampleManager {

    private final PluginService pluginService;

    private final HashMap<String, GuiSample> sampleHashMap = new HashMap<>();

    public SampleManager(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        sampleHashMap.put("online", new OnlineSample(pluginService));
    }

    public HashMap<String, GuiSample> getSampleMap() {
        return sampleHashMap;
    }
}

