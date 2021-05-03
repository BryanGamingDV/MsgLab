package me.bryangaming.chatlab.tasks;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.task.Task;
import me.bryangaming.chatlab.tasks.type.AnnouncerTask;
import me.bryangaming.chatlab.utils.module.ModuleType;

import java.util.HashMap;

public class TasksManager {

    private HashMap<String, Task> tasksList = new HashMap<>();

    private PluginService pluginService;

    public TasksManager(PluginService pluginService) {
        this.pluginService = pluginService;
        pluginService.getListManager().getModules().add("announcer");
        loadTasks(new AnnouncerTask("announcer", pluginService));
    }

    public void loadTasks(Task... taskTypes) {

        for (Task task : taskTypes) { ;
            if (!pluginService.getListManager().isEnabledOption(ModuleType.MODULE, task.getName())){
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
