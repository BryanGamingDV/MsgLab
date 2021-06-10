package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import java.util.ArrayList;

public class AchievementListener implements Listener {

    private PluginService pluginService;

    private final Configuration formatsFile;
    private final SenderManager senderManager;

    public AchievementListener(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }


    @EventHandler
    public void onPlayerAchievement(PlayerAchievementAwardedEvent event){

        String achievementName = event.getAchievement().name().toLowerCase();
        Player player = event.getPlayer();

        if (!formatsFile.getBoolean("achievements.enabled")){
            return;
        }

        if (formatsFile.getString("achievements." + achievementName) == null){
            return;
        }

        senderManager.sendMessageTo(new ArrayList<>(Bukkit.getOnlinePlayers()), formatsFile.getString("achievements.format")
                .replace("%achievement%", formatsFile.getString("achievements." + achievementName))
                .replace("%player%", player.getName()));

    }
}
