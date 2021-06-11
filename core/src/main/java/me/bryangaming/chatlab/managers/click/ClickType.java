package me.bryangaming.chatlab.managers.click;

public enum ClickType {
    GLOBAL("global"), WORLD("world");

    private final String path;

    ClickType(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
