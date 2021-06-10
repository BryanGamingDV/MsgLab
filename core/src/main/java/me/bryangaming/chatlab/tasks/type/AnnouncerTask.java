package me.bryangaming.chatlab.tasks.type;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.task.Task;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AnnouncerTask implements Task {

    private final String taskType;

    private final PluginService pluginService;
    private BukkitTask bukkitTask;

    public AnnouncerTask(String taskType, PluginService pluginService) {
        this.pluginService = pluginService;
        this.taskType = taskType;
    }

    public String getName() {
        return taskType;
    }

    public void loadTask() {

        Configuration configFile = pluginService.getFiles().getConfigFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        ConfigurationSection configurationSection = configFile.getConfigurationSection("modules.announcer.config.messages");

        if (configurationSection == null){
            return;
        }

        List<String> announcerList = new ArrayList<>(configurationSection.getKeys(false));

        String announcerType = configFile.getString("modules.announcer.config.type");

        Random random = new Random();

        AtomicInteger announcerID = new AtomicInteger(0);
        AtomicBoolean firstTime = new AtomicBoolean(true);

        bukkitTask = Bukkit.getServer().getScheduler().runTaskTimer(pluginService.getPlugin(), () -> {
            switch (announcerType.toLowerCase()) {
                case "random":
                    announcerID.set(random.nextInt(announcerList.size() - 1));
                    break;

                case "ordened":
                    if (firstTime.get()){
                        firstTime.set(false);
                        break;
                    }

                    announcerID.getAndIncrement();
                    if (announcerID.get() >= announcerList.size()) {
                        announcerID.set(0);
                        break;
                    }
                    break;

                default:
                    return;
            }
            Bukkit.getServer().getOnlinePlayers().forEach(player ->
                    senderManager.sendMessage(player, configFile.getStringList("modules.announcer.config.messages." + announcerList.get(announcerID.get()))));

        }, configFile.getInt("modules.announcer.config.interval") * 20L, configFile.getInt("modules.announcer.config.interval") * 20L);
    }

    public void cancelTask() {
        bukkitTask.cancel();
    }

    public void reloadTask() {
        cancelTask();
        loadTask();
    }

}


