package me.bryangaming.chatlab.common.wrapper;

import me.bryangaming.chatlab.common.wrapper.configuration.ConfigurationSectionWrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface YamlConfigurationWrapper {

    static YamlConfigurationWrapper loadConfiguration(File file){
        return null;
    }

    void load(File file) throws IOException;

    void save(File file) throws IOException;

    void reload();

    void set(String path, Object object);

    boolean contains(String path);

    String getString(String path);

    String getString(String path, String def);

    List<String> getStringList(String path);


    int getInt(String path);

    int getInt(String path, int def);

    boolean getBoolean(String path);

    boolean getBoolean(String path, boolean def);

    ConfigurationSectionWrapper getConfigurationSection(String path);



}
