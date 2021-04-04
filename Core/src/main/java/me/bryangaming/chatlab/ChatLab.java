package me.bryangaming.chatlab;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import me.bryangaming.chatlab.api.BasicAPIDesc;
import me.bryangaming.chatlab.api.ChatApiImpl;
import me.bryangaming.chatlab.modules.DataModule;
import me.bryangaming.chatlab.utils.UpdateCheck;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatLab extends JavaPlugin {

    private PluginService basicMsg;

    private BukkitAudiences bukkitAudiences;

    private static ChatApiImpl chatApiImpl;

    @Override
    public void onEnable() {

        loadKyori();
        registerManaging();
        registerPlaceholders();
        recoverStats();

        getLogger().info("Plugin created by " + getDescription().getAuthors() + "");
        getLogger().info("You are using version " + getDescription().getVersion() + ".");
        getLogger().info("If you want support, you can join in: https://discord.gg/wpSh4Bf4Es");

        basicMsg.getLogs().log("- Plugin successfull loaded.", 2);

    }

    public void loadKyori() {
        bukkitAudiences = BukkitAudiences.create(this);
    }

    public BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public static BasicAPIDesc getAPI() {
        return chatApiImpl;
    }

    public void onDisable() {
        getLogger().info("Thx for using this plugin <3. Goodbye!");
        bukkitAudiences.close();
    }

    public void registerManaging() {

        basicMsg = new PluginService(this);
        basicMsg.getLogs().log("Loading ChatApiImpl...");

        chatApiImpl = new ChatApiImpl(basicMsg);

        basicMsg.getLogs().log("Loaded.");

        if (!basicMsg.getFiles().getConfig().getString("version", "1.0").equalsIgnoreCase(getDescription().getVersion())) {
            getLogger().info("Error - Please reload the configuration!");
            getLogger().info("Error - You are using the latest version with an outdated path.");
            getLogger().info("Error - This can cause bugs..");
        }

        if (basicMsg.getFiles().getConfig().getBoolean("config.metrics")) {
            Metrics metrics = new Metrics(this, 10107);
        }
        if (basicMsg.getFiles().getConfig().getBoolean("config.update-check")) {
            getUpdateChecker();
        }

        DataModule dataModule = new DataModule(basicMsg);

    }

    public void recoverStats() {
        if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
            basicMsg.getLogs().log("The plugin was reloaded with /reload", 1);
            getLogger().info("Please don't use /reload to reload plugins, it can cause serious errors!");
            RecoverStats recoverStats = new RecoverStats(basicMsg);
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
            basicMsg.getLogs().log("PlaceholderAPI is not loaded !", 0);
        }
    }
}

