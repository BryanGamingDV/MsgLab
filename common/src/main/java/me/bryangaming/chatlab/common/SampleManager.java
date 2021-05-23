package me.bryangaming.chatlab.common;

import me.bryangaming.chatlab.common.gui.GuiSample;
import me.bryangaming.chatlab.common.gui.sample.OnlineSample;

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

