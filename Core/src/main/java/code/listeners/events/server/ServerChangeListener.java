package code.listeners.events.server;

import code.PluginService;
import code.data.ServerData;
import code.events.server.ChangeMode;
import code.events.server.ServerChangeEvent;
import code.managers.jq.JQFormat;
import code.managers.player.PlayerMessage;
import code.managers.player.PlayerStatic;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerChangeListener implements Listener {

    private PluginService pluginService;

    public ServerChangeListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onServerChangeEvent(ServerChangeEvent serverChangeEvent){


        JQFormat jqFormat = pluginService.getCache().getJQFormats().get(serverChangeEvent.getPlayerGroup());


        PlayerMessage playerMethods = pluginService.getPlayerMethods().getSender();

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(serverChangeEvent.getPlayer());
        Audience global = pluginService.getPlugin().getBukkitAudiences().players();

        Player sender = serverChangeEvent.getPlayer();

        if (serverChangeEvent.getChangeMode() == ChangeMode.FIRST_JOIN){

            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();

            switch (jqFormat.getFirstJoinFormat()) {
                case "none":
                    break;
                case "silent":
                    playerJoinEvent.setJoinMessage(null);
                    break;
                default:
                    playerJoinEvent.setJoinMessage(null);
                    global.sendMessage(PlayerStatic.convertTextToComponent(serverChangeEvent.getPlayer(), jqFormat.getFirstJoinFormat()));
            }

            if (jqFormat.getFirstJoinMotdList() != null) {
                for (String motdFormat : jqFormat.getFirstJoinMotdList()) {
                    if (motdFormat.startsWith("[LOOP")) {
                        String loopTest = motdFormat.split("]")[0];

                        int time = Integer.parseInt(loopTest.substring(3));

                        for (int id = 0; id > time; id++) {
                            player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                        }

                        player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat));
                    }
                }
            }

            if (jqFormat.getFirstJoinCommands() != null) {
                jqFormat.getFirstJoinCommands().forEach(sender::performCommand);
            }
            return;
        }


        if (serverChangeEvent.getChangeMode() == ChangeMode.JOIN){
            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();

            switch (jqFormat.getJoinFormat()) {
                case "none":
                    break;
                case "silent":
                    playerJoinEvent.setJoinMessage(null);
                    break;
                default:
                    playerJoinEvent.setJoinMessage(null);
                    global.sendMessage(PlayerStatic.convertTextToComponent(serverChangeEvent.getPlayer(), jqFormat.getJoinFormat()));
            }


            if (jqFormat.getJoinMotdList() != null) {
                for (String motdFormat : jqFormat.getJoinMotdList()) {
                    if (motdFormat.startsWith("[LOOP")) {
                        String loopTest = motdFormat.split("]")[0];

                        int time = Integer.parseInt(loopTest.substring(3));

                        for (int id = 0; id > time; id++) {
                            player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                        }

                        player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat));
                    }
                }
            }

            if (jqFormat.getJoinCommands() != null) {
                jqFormat.getJoinCommands().forEach(sender::performCommand);
            }
            return;
        }

        if (serverChangeEvent.getChangeMode() == ChangeMode.QUIT){

            PlayerQuitEvent playerQuitEvent = (PlayerQuitEvent) serverChangeEvent.getEvent();

            switch (jqFormat.getQuitFormat()) {
                case "none":
                    break;
                case "silent":
                    playerQuitEvent.setQuitMessage(null);
                    break;
                default:
                    playerQuitEvent.setQuitMessage(null);
                    global.sendMessage(PlayerStatic.convertTextToComponent(serverChangeEvent.getPlayer(), jqFormat.getQuitFormat()));
            }


            if (jqFormat.getQuitCommands() != null) {
                jqFormat.getQuitCommands().forEach(sender::performCommand);
            }
        }
    }
}
