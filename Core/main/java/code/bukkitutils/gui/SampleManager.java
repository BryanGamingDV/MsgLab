package code.bukkitutils.gui;

import code.Manager;

import java.util.HashMap;

public class SampleManager {

    private Manager manager;

    private OnlineSample onlineSample;
    private final HashMap<String, GuiSample> classHashMap = new HashMap<>();

    public SampleManager(Manager manager){
        this.manager = manager;
        setup();
    }

    public void setup(){
       classHashMap.put("online", new OnlineSample(manager));
    }

    public OnlineSample getOnlineSample(){
        return onlineSample;
    }

    public HashMap<String, GuiSample> getClassHashMap() {
        return classHashMap;
    }
}
