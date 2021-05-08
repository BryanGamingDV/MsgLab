package me.bryangaming.chatlab;

import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.modules.CheckModule;
import me.bryangaming.chatlab.modules.DataModule;
import me.bryangaming.chatlab.modules.RecoverDataModule;
import me.bryangaming.chatlab.utils.UpdateCheck;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatLab extends JavaPlugin {

    private PluginService basicMsg;
    private BukkitAudiences bukkitAudiences;


    @Override
    public void onEnable() {

        loadKyori();
        registerManaging();
        registerPlaceholders();
        recoverStats();

        getLogger().info("Plugin created by " + getDescription().getAuthors() + "");
        getLogger().info("You are using version " + getDescription().getVersion() + ".");
        getLogger().info("If you want support, you can join in: https://discord.gg/YjhubS3bWW");
        getLogger().info("If you want to go to the wiki, go in: https://bryangaming.gitbook.io/chatlab/");

        basicMsg.getLogs().log("- Plugin successfull loaded.", LoggerTypeEnum.SUCCESSFULL);

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

    public void registerManaging() {

        basicMsg = new PluginService(this);

        basicMsg.getLogs().log("Loading ChatApiImpl...");
        basicMsg.getLogs().log("Loaded.");

        if (!basicMsg.getFiles().getConfigFile().getString("version", "1.0").equalsIgnoreCase(getDescription().getVersion())) {
            getLogger().info("Error - Please reload the configuration!");
            getLogger().info("Error - You are using the latest version with an outdated path.");
            getLogger().info("Error - This can cause bugs..");
        }

        if (basicMsg.getFiles().getConfigFile().getBoolean("config.metrics")) {
            Metrics metrics = new Metrics(this, 10107);
        }
        if (basicMsg.getFiles().getConfigFile().getBoolean("config.update-check")) {
            getUpdateChecker();
        }

        initModules(
                new RecoverDataModule(getManager()),
                new DataModule(getManager()),
                new CheckModule(getManager()));
    }

    public void recoverStats() {
        if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
            basicMsg.getLogs().log("The plugin was reloaded with /reload", LoggerTypeEnum.WARNING);
            getLogger().info("Please don't use /reload to reload plugins, it can cause serious errors!");
        }
    }

    public void initModules(Module... modules) {
        for (Module module : modules) {
            module.start();
        }
    }

    public PluginService getManager() {
        return basicMsg;
    }

    public void getUpdateChecker() {
        getLogger().info("Checking updating checker..");
        UpdateCheck.init(this, 84926);
    }

    public void registerPlaceholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI hooked!");
            basicMsg.getLogs().log("PlaceholderAPI loaded!");
        } else {
            basicMsg.getLogs().log("PlaceholderAPI is not loaded !", LoggerTypeEnum.WARNING);
        }
    }
}

