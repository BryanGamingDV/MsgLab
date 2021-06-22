package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.events.server.ChangeMode;
import me.bryangaming.chatlab.events.server.ServerChangeEvent;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

public class ServerChangeListener implements Listener {

    private final PluginService pluginService;

    private final ActionManager actionManager;

    public ServerChangeListener(PluginService pluginService) {
        this.pluginService = pluginService;

        this.actionManager = pluginService.getPlayerManager().getActionManager();
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
                if (jqData.getFirstJoinType() != null) {
                    switch (jqData.getFirstJoinType()) {
                        case "none":
                            break;
                        case "silent":
                            playerJoinEvent.setJoinMessage(null);
                            break;
                        case "orderly":
                            playerJoinEvent.setJoinMessage(null);
                            if (jqData.firstJoinIdIsTheMax(jqData.getFirstJoinFormat().size())){
                                jqData.clearFirstJoinNextId();
                            }

                            global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getFirstJoinFormat().get(jqData.getFirstJoinNextId())));
                            jqData.sumFirstJoinNextId();
                        case "random":
                            Random random = new Random();
                            global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getFirstJoinFormat().get(random.nextInt() * (jqData.getFirstJoinFormat().size() - 1))));

                    }

                }
            }

            if (jqData.getFirstJoinMotdList() != null) {

                if (!jqData.getFirstJoinHook().equalsIgnoreCase("none")) {
                    for (String motdFormat : jqData.getFirstJoinMotdList()) {
                        if (motdFormat.startsWith("[LOOP")) {
                            String loopTest = motdFormat.split("]")[0];

                            int time = Integer.parseInt(loopTest.substring(3));

                            for (int id = 0; id < time; id++) {
                                player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                            }
                            continue;
                        }
                        player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat));
                    }
                }
            }

            if (jqData.getFirstJoinActions() != null) {
                actionManager.execute(sender, jqData.getFirstJoinActions());
            }
            return;
        }


        if (serverChangeEvent.getChangeMode() == ChangeMode.JOIN) {
            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();

            if (jqData.getJoinFormat() != null) {
                if (jqData.getJoinType() != null) {
                    switch (jqData.getJoinType()) {
                        case "none":
                            break;
                        case "silent":
                            playerJoinEvent.setJoinMessage(null);
                            break;
                        case "orderly":
                            playerJoinEvent.setJoinMessage(null);
                            if (jqData.joinIdIsTheMax(jqData.getJoinFormat().size())) {
                                jqData.clearJoinNextId();
                            }

                            global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getJoinFormat().get(jqData.getJoinNextId())));
                            jqData.sumJoinNextId();
                        case "random":
                            Random random = new Random();
                            global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getJoinFormat().get(random.nextInt() * (jqData.getJoinFormat().size() - 1))));
                    }
                }
            }
              if (jqData.getJoinMotdList() != null) {
                if (!jqData.getJoinHook().equalsIgnoreCase("none")) {
                    for (String motdFormat : jqData.getJoinMotdList()) {
                        if (motdFormat.startsWith("[LOOP")) {
                            String loopTest = motdFormat.split("]")[0];

                            int time = Integer.parseInt(loopTest.substring(3));

                            for (int id = 0; id < time; id++) {
                                player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                            }
                            continue;
                        }

                        player.sendMessage(TextUtils.convertTextToComponent(sender, motdFormat));
                    }
                }
            }

            if (jqData.getJoinActions() != null) {
                actionManager.execute(sender, jqData.getJoinActions());
            }
            return;
        }

        if (serverChangeEvent.getChangeMode() == ChangeMode.QUIT) {

            PlayerQuitEvent playerQuitEvent = (PlayerQuitEvent) serverChangeEvent.getEvent();

            if (jqData.getQuitFormat() != null) {
                if (jqData.getQuitType() != null) {
                    switch (jqData.getQuitType()) {
                        case "none":
                            break;
                        case "silent":
                            playerQuitEvent.setQuitMessage(null);
                            break;
                        case "orderly":
                            playerQuitEvent.setQuitMessage(null);
                            if (jqData.quitIdIsTheMax(jqData.getQuitFormat().size())) {
                                jqData.clearQuitNextId();
                            }

                            global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getQuitFormat().get(jqData.getQuitNextId())));
                            jqData.sumQuitNextId();
                        case "random":
                            Random random = new Random();
                            global.sendMessage(TextUtils.convertTextToComponent(serverChangeEvent.getPlayer(), jqData.getQuitFormat().get(random.nextInt() * (jqData.getQuitFormat().size() - 1))));
                    }
                }
            }


            if (jqData.getQuitActions() != null) {
                actionManager.execute(sender, jqData.getQuitActions());
            }
        }
    }
}
