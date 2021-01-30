package code.bukkitutils.gui;

import code.PluginService;

import java.util.HashMap;

public class SampleManager {

    private PluginService pluginService;

    private OnlineSample onlineSample;
    private final HashMap<String, GuiSample> classHashMap = new HashMap<>();

    public SampleManager(PluginService pluginService){
        this.pluginService = pluginService;
        setup();
    }

    public void setup(){
       classHashMap.put("online", new OnlineSample(pluginService));
    }

    public OnlineSample getOnlineSample(){
        return onlineSample;
    }

    public HashMap<String, GuiSample> getClassHashMap() {
        return classHashMap;
    }
}
