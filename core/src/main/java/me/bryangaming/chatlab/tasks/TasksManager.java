package me.bryangaming.chatlab.tasks;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.task.Task;
import me.bryangaming.chatlab.tasks.type.AnnouncerTask;

import java.util.HashMap;

public class TasksManager {

    private final HashMap<String, Task> tasksList = new HashMap<>();

    private final PluginService pluginService;

    public TasksManager(PluginService pluginService) {
        this.pluginService = pluginService;
        loadTasks(new AnnouncerTask("announcer", pluginService));
        pluginService.getLogs().log("Loaded tasks");
    }

    public void loadTasks(Task... taskTypes) {

        for (Task task : taskTypes) { ;
            if (!pluginService.getFiles().getConfigFile().getBoolean("modules." + task.getName() + ".enabled")){
                continue;
            }

            task.loadTask();
            tasksList.put(task.getName(), task);
        }
    }

    public Task getTask(String taskType) {
        return tasksList.get(taskType);
    }

    public void reloadTasks() {
        for (Task task : tasksList.values()) {
            task.reloadTask();
        }
    }

}
