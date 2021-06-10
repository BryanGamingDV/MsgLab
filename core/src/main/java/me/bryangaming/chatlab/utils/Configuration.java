package me.bryangaming.chatlab.utils;

import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public final class Configuration extends YamlConfiguration {

    private final String fileName;
    private final Plugin plugin;
    private final File file;

    public Configuration(Plugin plugin, String fileName, String fileExtension,
                         File folder) {
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.plugin = plugin;

        this.file = new File(folder, this.fileName);
        this.createFile();
    }

    public Configuration(Plugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public Configuration(Plugin plugin, String fileName, String fileExtension) {
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

    public String getColoredString(String path) {
        return TextUtils.setColor(getString(path));
    }

    public List<String> getColoredStringList(String path) {

        List<String> stringList = getStringList(path);
        stringList.replaceAll(TextUtils::setColor);

        return stringList;
    }
}