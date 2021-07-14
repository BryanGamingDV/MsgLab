package me.bryangaming.chatlab;

import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.modules.CheckModule;
import me.bryangaming.chatlab.modules.DataModule;
import me.bryangaming.chatlab.modules.RecoverDataModule;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.UpdateCheck;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;

public class ChatLab extends JavaPlugin {

    private PluginService chatLab;
    private BukkitAudiences bukkitAudiences;

    @Override
    public void onEnable() {

        loadKyori();
        registerServices();
        registerPlaceholders();
        recoverStats();

        getLogger().info("Plugin created by " + getDescription().getAuthors() + "");
        getLogger().info("You are using version " + getDescription().getVersion() + ".");
        getLogger().info("If you want support, you can join in: https://discord.gg/YjhubS3bWW");
        getLogger().info("If you want to go to the wiki, go in: https://bryangaming.gitbook.io/chatlab/");

        chatLab.getDebugger().log("- Plugin successful loaded.", LoggerTypeEnum.SUCCESSFULL);
    }

    public void loadKyori() {
        bukkitAudiences = BukkitAudiences.create(this);
    }

    public BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public void onDisable() {
        getLogger().info("Thx for using this plugin <3. Goodbye!");
        bukkitAudiences.close();
    }

    public void registerServices() {
        chatLab = new PluginService(this);
        Configuration configFile = chatLab.getFiles().getConfigFile();

        if (!configFile.getString("config-version", "1.0").equalsIgnoreCase("2.3")) {
            getLogger().info("Error - Please reload the configuration!");
            getLogger().info("Error - You are using the latest version with an outdated path.");
            getLogger().info("Error - This can cause bugs..");
        }

        if (configFile.getBoolean("options.metrics")) {
            Metrics metrics = new Metrics(this, 10107);
        }
        if (configFile.getBoolean("options.update-check")) {
            getUpdateChecker();
        }

        initModules(
                new RecoverDataModule(chatLab),
                new DataModule(chatLab),
                new CheckModule(chatLab));

        if (hasLockLogin()) {
            getLogger().info("LockLogin found, initializing advanced hook");

            File pluginsFolder = new File(getServer().getWorldContainer(), "plugins");
            File lockloginModules = new File(pluginsFolder + File.separator + "LockLogin" + File.separator + "plugin", "modules");

            try {
                File pluginJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile().replaceAll("%20", " "));
                File copyJar = new File(lockloginModules, "LockLoginHook.jar");

                if (!copyJar.getParentFile().exists())
                    Files.createDirectories(copyJar.getParentFile().toPath());

                Files.copy(pluginJar.toPath(), copyJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Throwable ex) {
                getLogger().log(Level.SEVERE, "Failed to hook into LockLogin", ex);
            }
        }
    }

    public void recoverStats() {
        if (Bukkit.getServer().getOnlinePlayers().size() > 0) {

            chatLab.getDebugger().log("The plugin was reloaded with /reload", LoggerTypeEnum.WARNING);
            getLogger().info("Please don't use /reload to reload plugins, it can cause serious errors!");

        }
    }

    public void initModules(Module... modules) {
        for (Module module : modules) {
            module.start();
        }
    }

    public void getUpdateChecker() {
        getLogger().info("Checking updating checker..");
        UpdateCheck.init(this, 84926);
    }

    public void registerPlaceholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI hooked!");
            chatLab.getDebugger().log("PlaceholderAPI loaded!");
        } else {
            chatLab.getDebugger().log("PlaceholderAPI is not loaded !", LoggerTypeEnum.WARNING);
        }
    }

    private boolean hasLockLogin() {
        File pluginsFolder = new File(getServer().getWorldContainer(), "plugins");
        File[] files = pluginsFolder.listFiles();
        if (files != null) {
            for (File plugin : files) {
                if (plugin.isFile() && plugin.getName().endsWith(".jar")) {
                    try {
                        JarFile jar = new JarFile(plugin);
                        ZipEntry pluginYML = jar.getEntry("plugin.yml");
                        if (pluginYML != null) {
                            InputStream input = jar.getInputStream(pluginYML);
                            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
                            String name = yaml.getString("name", "");
                            assert name != null;

                            if (name.equalsIgnoreCase("locklogin")) {
                                return true;
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }
        }

        return false;
    }
}

