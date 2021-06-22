package me.bryangaming.chatlab.listeners.login;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.events.PlayerLoginEvent;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.TextUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLoginListener implements Listener {


    private final PluginService pluginService;

    public PlayerLoginListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event){

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

        Player sender = event.getPlayer();

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        String playerRank = groupManager.getJQGroup(sender);

        JQData jqData = pluginService.getCache().getJQFormats().get(playerRank);

        if (!event.getPlayer().hasPlayedBefore()){
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
        }else{
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
}
