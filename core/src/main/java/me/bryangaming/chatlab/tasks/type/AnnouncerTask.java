package me.bryangaming.chatlab.tasks.type;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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

        ConfigurationSection announcerMessages = configFile.getConfigurationSection("modules.announcer.announcers.messages");

        if (announcerMessages == null){
            return;
        }

        List<String> announcerList = new ArrayList<>(announcerMessages.getKeys(false));

        String announcerType = configFile.getString("modules.announcer.announcers.type");
        long announcerDelay = configFile.getInt("modules.announcer.global-interval") * 20L;

        Random random = new Random();

        AtomicInteger announcerID = new AtomicInteger(0);
        AtomicBoolean firstTime = new AtomicBoolean(true);

        bukkitTask = Bukkit.getServer().getScheduler().runTaskTimer(pluginService.getPlugin(), () -> {

            switch (announcerType.toLowerCase()) {
                case "random":
                    announcerID.set(random.nextInt(announcerList.size() - 1));
                    break;

                case "ordered":
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
                    senderManager.sendMessage(player, configFile.getStringList("modules.announcer.announcers.messages." + announcerList.get(announcerID.get()))));

        }, announcerDelay, announcerDelay);
    }

    public void cancelTask() {
        if (bukkitTask == null){
            return;
        }
        bukkitTask.cancel();
    }

    public void reloadTask() {
        cancelTask();
        loadTask();
    }

}


