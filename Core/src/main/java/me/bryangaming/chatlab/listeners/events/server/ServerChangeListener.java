package atogesputo.bryangaming.chatlab.listeners.events.server;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.data.JQFormat;
import atogesputo.bryangaming.chatlab.events.server.ChangeMode;
import atogesputo.bryangaming.chatlab.events.server.ServerChangeEvent;
import atogesputo.bryangaming.chatlab.managers.player.PlayerStatic;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerChangeListener implements Listener {

    private PluginService pluginService;

    public ServerChangeListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onServerChangeEvent(ServerChangeEvent serverChangeEvent){


        JQFormat jqFormat = pluginService.getCache().getJQFormats().get(serverChangeEvent.getPlayerGroup());


        Audience player = pluginService.getPlugin().getBukkitAudiences().player(serverChangeEvent.getPlayer());
        Audience global = pluginService.getPlugin().getBukkitAudiences().players();

        Player sender = serverChangeEvent.getPlayer();

        if (serverChangeEvent.getChangeMode() == ChangeMode.FIRST_JOIN){

            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();

            if (jqFormat.getFirstJoinFormat() != null) {
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
            }

            if (jqFormat.getFirstJoinMotdList() != null) {
                for (String motdFormat : jqFormat.getFirstJoinMotdList()) {
                    if (motdFormat.startsWith("[LOOP")) {
                        String loopTest = motdFormat.split("]")[0];

                        int time = Integer.parseInt(loopTest.substring(3));

                        for (int id = 0; id > time; id++) {
                            player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                        }
                        continue;
                    }
                    player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat));
                }
            }

            if (jqFormat.getFirstJoinCommands() != null) {
                jqFormat.getFirstJoinCommands().forEach(sender::performCommand);
            }
            return;
        }


        if (serverChangeEvent.getChangeMode() == ChangeMode.JOIN){
            PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) serverChangeEvent.getEvent();


            if (jqFormat.getJoinFormat() != null) {
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
            }


            if (jqFormat.getJoinMotdList() != null) {
                for (String motdFormat : jqFormat.getJoinMotdList()) {
                    if (motdFormat.startsWith("[LOOP")) {
                        String loopTest = motdFormat.split("]")[0];

                        int time = Integer.parseInt(loopTest.substring(3));

                        for (int id = 0; id > time; id++) {
                            player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat.substring(loopTest.length())));
                        }
                        continue;
                    }

                    player.sendMessage(PlayerStatic.convertTextToComponent(sender, motdFormat));
                }
            }

            if (jqFormat.getJoinCommands() != null) {
                jqFormat.getJoinCommands().forEach(sender::performCommand);
            }
            return;
        }

        if (serverChangeEvent.getChangeMode() == ChangeMode.QUIT){

            PlayerQuitEvent playerQuitEvent = (PlayerQuitEvent) serverChangeEvent.getEvent();

            if (jqFormat.getQuitFormat() != null) {
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
            }


            if (jqFormat.getQuitCommands() != null) {
                jqFormat.getQuitCommands().forEach(sender::performCommand);
            }
        }
    }
}
