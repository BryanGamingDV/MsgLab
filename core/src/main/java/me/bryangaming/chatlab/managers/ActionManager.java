package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.TextUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.sound.sampled.AudioInputStream;
import java.time.Duration;
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

            if (actions.startsWith("[TITLE]")){
                String[] titlePath = actions.substring(7).split(";");

                Audience audience = pluginService.getPlugin().getBukkitAudiences().player(player);
                audience.showTitle(Title.title(

                        TextUtils.convertTextToComponent(player, titlePath[0]),
                        TextUtils.convertTextToComponent(player, titlePath[1]),

                        Title.Times.of(Duration.ofSeconds(Integer.parseInt(titlePath[2])),
                        Duration.ofSeconds(Integer.parseInt(titlePath[3])),
                        Duration.ofSeconds(Integer.parseInt(titlePath[4])))));
                continue;
            }

            if (actions.startsWith("[ACTIONBAR]")){
                String[] actionBarPath = actions.substring(11).split(";");

                Audience audience = pluginService.getPlugin().getBukkitAudiences().player(player);
                audience.sendActionBar(TextUtils.convertTextToComponent(player, actionBarPath[0]));
                continue;
            }

            if (actions.startsWith("[BOSSBAR]")){
                String[] bossBarPath = actions.substring(9).split(";");


                Audience audience = pluginService.getPlugin().getBukkitAudiences().player(player);
                audience.showBossBar(BossBar.bossBar(
                        TextUtils.convertTextToComponent(player, bossBarPath[0]),
                        Float.parseFloat(bossBarPath[1]),
                        BossBar.Color.valueOf(bossBarPath[2].toUpperCase()),
                        BossBar.Overlay.valueOf(bossBarPath[3].toUpperCase())
                ));
                continue;
            }

        }
    }
}
