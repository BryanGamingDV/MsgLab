package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ActionManager {

    private final PluginService pluginService;
    private final SenderManager senderManager;

    public ActionManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    public void execute(Player player, List<String> listString){
        for (String actions : listString){
            if (actions.startsWith("[MESSAGE]")){
                senderManager.sendMessage(player, actions.substring(9));
                continue;
            }

            if (actions.startsWith("[CONSOLECMD]")){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actions.substring(12));
                continue;
            }

            if (actions.startsWith("[LIGHTING]")){
                player.getWorld().strikeLightning(player.getLocation());
                continue;
            }

            if (actions.startsWith("[BROADCAST]")){
                Bukkit.broadcastMessage(actions.substring(11));
                continue;
            }

            if (actions.startsWith("[BROADCASTWORLD]")){
                player.getWorld().getPlayers().forEach(worldPlayer -> {
                    worldPlayer.sendMessage(actions.substring(16));
                });
                continue;
            }

            if (actions.startsWith("[SOUND]")){
                String[] soundPath = actions.substring(7).split(";");

                Sound sound;
                try {
                    sound = Sound.valueOf(soundPath[0]);
                }catch (IllegalArgumentException illegalArgumentException){
                    pluginService.getPlugin().getLogger().info("Error! The sound no exists: " + soundPath[0]);
                    continue;
                }
                player.playSound(player.getLocation(), sound, Float.parseFloat(soundPath[1]), Float.parseFloat(soundPath[2]));
                continue;
            }

            if (actions.startsWith("[BROADCASTSOUND]")) {
                String[] soundPath = actions.substring(16).split(";");

                Sound sound;

                try {
                    sound = Sound.valueOf(soundPath[0]);
                } catch (IllegalArgumentException illegalArgumentException) {
                    pluginService.getPlugin().getLogger().info("Error! The sound no exists: " + soundPath[0]);
                    continue;
                }
                Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                    onlinePlayer.playSound(player.getLocation(), sound, Float.parseFloat(soundPath[1]), Float.parseFloat(soundPath[2]));
                });
                continue;
            }

            if (actions.startsWith("[EFFECT]")){
                String[] effectPath = actions.substring(8).split(";");

                PotionEffectType effect = PotionEffectType.getByName(effectPath[0]);

                if (effect == null){
                    return;
                }

                player.addPotionEffect(new PotionEffect(effect, Integer.parseInt(effectPath[1]), Integer.parseInt(effectPath[2])));
                continue;
            }
        }
    }
}
