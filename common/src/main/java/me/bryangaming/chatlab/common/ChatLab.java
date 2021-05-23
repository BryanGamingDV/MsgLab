package me.bryangaming.chatlab.common;

import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.common.debug.LoggerTypeEnum;
import me.bryangaming.chatlab.common.modules.CheckModule;
import me.bryangaming.chatlab.common.modules.DataModule;
import me.bryangaming.chatlab.common.modules.RecoverDataModule;
import me.bryangaming.chatlab.common.utils.UpdateCheck;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginBaseWrapper;
import me.bryangaming.chatlab.common.wrapper.plugin.PluginDescriptionWrapper;

import java.util.logging.Logger;

public class ChatLab{

    private PluginService basicMsg;
    private final PluginBaseWrapper pluginWrapper;

    private final Logger logger;
    private final PluginDescriptionWrapper description;

    public ChatLab(PluginBaseWrapper pluginBaseWrapper) {
        this.pluginWrapper = pluginBaseWrapper;

        this.logger = pluginBaseWrapper.getLogger();
        this.description = pluginBaseWrapper.getDescription();
    }


    public void onEnable() {

        registerManaging();
        registerPlaceholders();
        recoverStats();

        logger.info("Plugin created by " + description.getAuthor() + "");
        logger.info("You are using version " + description.getVersion() + ".");
        logger.info("If you want support, you can join in: https://discord.gg/YjhubS3bWW");
        logger.info("If you want to go to the wiki, go in: https://bryangaming.gitbook.io/chatlab/");

        basicMsg.getLogs().log("- Plugin successfull loaded.", LoggerTypeEnum.SUCCESSFULL);

    }


    public void onDisable() {
        logger.info("Thx for using this plugin <3. Goodbye!");
    }


    public void registerManaging() {

        basicMsg = new PluginService(pluginWrapper);

        basicMsg.getLogs().log("Loading ChatApiImpl...");
        basicMsg.getLogs().log("Loaded.");

        if (!basicMsg.getFiles().getConfigFile().getString("version", "1.0").equalsIgnoreCase(description.getVersion())) {
            logger.info("Error - Please reload the configuration!");
            logger.info("Error - You are using the latest version with an outdated path.");
            logger.info("Error - This can cause bugs..");
        }

        if (basicMsg.getFiles().getConfigFile().getBoolean("config.metrics")) {
           pluginWrapper.loadMetrics(10107);
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
        if (ServerWrapper.getData().getOnlineSize() > 0) {
            basicMsg.getLogs().log("The plugin was reloaded with /reload", LoggerTypeEnum.WARNING);
            logger.info("Please don't use /reload to reload plugins, it can cause serious errors!");
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
        logger.info("Checking updating checker..");
        UpdateCheck.init(pluginWrapper, 84926);
    }

    public void registerPlaceholders() {
        if (ServerWrapper.getData().isPluginEnabled("PlaceholderAPI")) {
            logger.info("PlaceholderAPI hooked!");
            basicMsg.getLogs().log("PlaceholderAPI loaded!");
        } else {
            basicMsg.getLogs().log("PlaceholderAPI is not loaded !", LoggerTypeEnum.WARNING);
        }
    }
}

