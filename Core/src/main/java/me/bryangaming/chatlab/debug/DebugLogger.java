package me.bryangaming.chatlab.debug;

import me.bryangaming.chatlab.ChatLab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugLogger {

    private final ChatLab plugin;

    private File file;

    public DebugLogger(ChatLab plugin) {
        this.plugin = plugin;
        setup();
        log("Loading plugin..");
    }

    public void log(String string){
        getLogger(string, "LOG");
    }

    public void log(String string, LoggerTypeEnum loggerTypeEnum) {
        switch (loggerTypeEnum) {
            case WARNING:
                break;
            case ERROR:
                plugin.getLogger().info("A error ocurred. Please check debug");
                break;
            case SUCCESSFULL:
                plugin.getLogger().info("Plugin successfully loaded!");
                break;
        }

        getLogger(string, loggerTypeEnum.getName());
    }

    public void setup() {
        file = new File(plugin.getDataFolder(), "logs.yml");
        file.mkdirs();

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            plugin.getLogger().info("Logs created!");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    private void getLogger(String string, String typeName) {
        Date now = new Date();

        try {
            FileWriter fw = new FileWriter(plugin.getDataFolder() + "/logs.yml", true);
            BufferedWriter writer = new BufferedWriter(fw);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            writer.write("[" + format.format(now) + " " + typeName + "] " + string);
            writer.newLine();
            writer.flush();
            writer.close();

        } catch (IOException io) {
            io.printStackTrace();

        }

    }
}
