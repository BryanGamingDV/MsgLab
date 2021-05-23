package me.bryangaming.chatlab.common.utils;

import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.wrapper.YamlConfigurationWrapper;
import me.bryangaming.chatlab.common.wrapper.configuration.ConfigurationSectionWrapper;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginBaseWrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public final class Configuration {

    private final String fileName;
    private final PluginBaseWrapper plugin;
    private final File file;

    private YamlConfigurationWrapper yamlConfigurationWrapper;
    public Configuration(PluginBaseWrapper plugin, String fileName, String fileExtension,
                         File folder) {
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.plugin = plugin;

        this.file = new File(folder, this.fileName);
        this.createFile();
    }

    public Configuration(PluginBaseWrapper plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public Configuration(PluginBaseWrapper plugin, String fileName, String fileExtension) {
        this(plugin, fileName, fileExtension, plugin.getDataFolder());
    }

    private void createFile() {
        try {
            if (file.exists()) {

                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    this.save(file);
                }

                load(file);
                return;
            }

            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                save(file);
            }

            load(file);
            this.yamlConfigurationWrapper = YamlConfigurationWrapper.loadConfiguration(file);

        } catch (InvalidConfigurationException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Creation of Configuration '" + fileName + "' failed.", e);
        }

    }


    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Save of the file '" + fileName + "' failed.", e);
        }
    }

    public void reload() {
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Reload of the file '" + fileName + "' failed.", e);
        }
    }

    public void save(File file) throws IOException{
        yamlConfigurationWrapper.save(file);
    }
    public void load(File file) throws IOException{
        yamlConfigurationWrapper.load(file);
    }

    public boolean contains(String path){
        return yamlConfigurationWrapper.contains(path);
    }

    public String getString(String path){
        return yamlConfigurationWrapper.getString(path);
    }

    public String getString(String path, String def){
        return yamlConfigurationWrapper.getString(path, def);
    }

    public List<String> getStringList(String path){
        return yamlConfigurationWrapper.getStringList(path);
    }
    public int getInt(String path){
        return yamlConfigurationWrapper.getInt(path);
    }

    public void set(String path, Object object){
        yamlConfigurationWrapper.set(path, object);
    }
    public int getInt(String path, int def){
        return yamlConfigurationWrapper.getInt(path, def);
    }

    public boolean getBoolean(String path){
        return yamlConfigurationWrapper.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def){
        return yamlConfigurationWrapper.getBoolean(path, def);
    }

    public ConfigurationSectionWrapper getConfigurationSection(String path){
        return yamlConfigurationWrapper.getConfigurationSection(path);
    }


    public String getColoredString(String path) {
        return TextUtils.setColor(getString(path));
    }

    public List<String> getColoredStringList(String path) {

        List<String> stringList = getStringList(path);
        stringList.replaceAll(TextUtils::setColor);

        return stringList;
    }
}