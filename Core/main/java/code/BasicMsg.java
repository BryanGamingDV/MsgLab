package code;

import code.api.BasicAPI;
import code.api.BasicAPImpl;
import code.utils.UpdateCheck;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class BasicMsg extends JavaPlugin {

    private Manager basicMsg;

    private static BasicAPImpl api;

    @Override
    public void onEnable() {


        Bukkit.getServer().getName();
        registerManaging();
        registerPlaceholders();
        recoverStats();

        getLogger().info("Plugin created by "+getDescription().getAuthors() + "");
        getLogger().info("You are using version " + getDescription().getVersion() + ".");
        getLogger().info("If you want support, you can join in: https://discord.gg/wpSh4Bf4Es");

        basicMsg.getLogs().log("- Plugin successfull loaded.", 2);

    }

    public static BasicAPImpl getAPI(){
        return api;
    }

    public void onDisable() {
        getLogger().info("Thx for using this plugin <3.");
        getDisableMessage();
    }

    public void registerManaging() {

        basicMsg = new Manager(this);
        basicMsg.getLogs().log("Loading API...");
        api = new BasicAPImpl(basicMsg);
        basicMsg.getLogs().log("Loaded.");

        if (basicMsg.getFiles().getConfig().getBoolean("config.metrics")) {
            Metrics metrics = new Metrics(this, 10107);
        }
        if (basicMsg.getFiles().getConfig().getBoolean("config.update-check")){
            getUpdateChecker();
        }

    }

    public void recoverStats(){
        if (Bukkit.getServer().getOnlinePlayers().size() > 0){
            basicMsg.getLogs().log("The plugin was reloaded with /reload", 1);
            getLogger().info("Please don't use /reload to reload plugin, it can cause serious errors!");
            RecoverStats recoverStats = new RecoverStats(basicMsg);
        }
    }

    public Manager getManager(){
        return basicMsg;
    }

    public void getUpdateChecker(){
        getLogger().info("Checking updating checker..");
        UpdateCheck.init(this, 84926);
    }
    public void registerPlaceholders(){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI hooked!");
            basicMsg.getLogs().log("PlaceholderAPI loaded!");
        }else{
            basicMsg.getLogs().log("PlaceholderAPI is not loaded !", 0);
        }
    }
    public void getDisableMessage(){
        int number = (int) (Math.random() * 3 + 1);

        if (number == 1) {
            getLogger().info("Goodbye!");
            return;
        }

        if (number == 2) {
            getLogger().info("See you later!");
            return;
        }

        if (number == 3) {
            getLogger().info("Bye!");
            return;
        }

        getLogger().info("You shouldn't watch this..");
    }
}

