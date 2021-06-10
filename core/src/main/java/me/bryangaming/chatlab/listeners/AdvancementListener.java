package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.checkerframework.checker.lock.qual.EnsuresLockHeld;

import java.util.ArrayList;

public class AdvancementListener implements Listener {

    private PluginService pluginService;

    private final Configuration formatsFile;

    private final SenderManager senderManager;

    public AdvancementListener(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event){

        if (!formatsFile.getBoolean("advancements.enabled")){
            return;
        }
        String achievementName = event.getAdvancement().getKey().getKey().split("/")[1];

        if (formatsFile.getString("advancements." + achievementName) == null){
            return;
        }

        senderManager.sendMessageTo(new ArrayList<>(Bukkit.getOnlinePlayers()), formatsFile.getString("advancements.format")
                .replace(achievementName, formatsFile.getString("advancements." + achievementName)));
    }
}
