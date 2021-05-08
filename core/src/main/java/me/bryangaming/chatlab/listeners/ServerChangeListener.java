package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.events.server.ChangeMode;
import me.bryangaming.chatlab.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.utils.string.TextUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerChangeListener implements Listener {

    private PluginService pluginService;

    public ServerChangeListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onServerChangeEvent(ServerChangeEvent serverChangeEvent) {

        JQData jqData = pluginService.getCache().getJQFormats().get(serverChangeEvent.getPlayerGroup());

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(serverChangeEvent.getPlayer());
        Audience global = pluginService.getPlugin().getBukkitAudiences().players();

        Player sender = serverChangeEvent.getPlayer();

        if (serverChangeEvent.getChangeMode() == ChangeMode.FIRST_JOIN) {

            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();

            if (jqData.getFirstJoinFormat() != null) {
                switch (jqData.getFirstJoinFormat()) {
                    case "none":
                        break;
                    case "silent":
                        playerJoinEvent.setJoinMessage(null);
                        break;
                    default:
                        playerJoinEvent.setJoinMessage(null);
                        global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getFirstJoinFormat()));
                }
            }

            if (jqData.getFirstJoinMotdList() != null) {
                for (String motdFormat : jqData.getFirstJoinMotdList()) {
                    if (motdFormat.startsWith("[LOOP")) {
                        String loopTest = motdFormat.split("]")[0];

                        int time = Integer.parseInt(loopTest.substring(3));

                        for (int id = 0; id > time; id++) {
                            player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                        }
                        continue;
                    }
                    player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat));
                }
            }

            if (jqData.getFirstJoinCommands() != null) {
                jqData.getFirstJoinCommands().forEach(sender::performCommand);
            }
            return;
        }


        if (serverChangeEvent.getChangeMode() == ChangeMode.JOIN) {
            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();


            if (jqData.getJoinFormat() != null) {
                switch (jqData.getJoinFormat()) {
                    case "none":
                        break;
                    case "silent":
                        playerJoinEvent.setJoinMessage(null);
                        break;
                    default:
                        playerJoinEvent.setJoinMessage(null);
                        global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getJoinFormat()));
                }
            }


            if (jqData.getJoinMotdList() != null) {
                for (String motdFormat : jqData.getJoinMotdList()) {
                    if (motdFormat.startsWith("[LOOP")) {
                        String loopTest = motdFormat.split("]")[0];

                        int time = Integer.parseInt(loopTest.substring(3));

                        for (int id = 0; id > time; id++) {
                            player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                        }
                        continue;
                    }

                    player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat));
                }
            }

            if (jqData.getJoinCommands() != null) {
                jqData.getJoinCommands().forEach(sender::performCommand);
            }
            return;
        }

        if (serverChangeEvent.getChangeMode() == ChangeMode.QUIT) {

            PlayerQuitEvent playerQuitEvent = (PlayerQuitEvent) serverChangeEvent.getEvent();

            if (jqData.getQuitFormat() != null) {
                switch (jqData.getQuitFormat()) {
                    case "none":
                        break;
                    case "silent":
                        playerQuitEvent.setQuitMessage(null);
                        break;
                    default:
                        playerQuitEvent.setQuitMessage(null);
                        global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getQuitFormat()));
                }
            }


            if (jqData.getQuitCommands() != null) {
                jqData.getQuitCommands().forEach(sender::performCommand);
            }
        }
    }
}
