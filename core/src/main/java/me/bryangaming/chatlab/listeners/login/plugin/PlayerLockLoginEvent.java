package me.bryangaming.chatlab.listeners.login.plugin;


import eu.locklogin.api.module.plugin.api.event.ModuleEventHandler;
import eu.locklogin.api.module.plugin.api.event.user.UserAuthenticateEvent;
import eu.locklogin.api.module.plugin.api.event.util.EventListener;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.text.TextUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Random;

public class PlayerLockLoginEvent implements EventListener {

    private final PluginService pluginService;
    private final Configuration formatsFile;
    private final ActionManager actionManager;

    public PlayerLockLoginEvent(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
        this.actionManager = pluginService.getPlayerManager().getActionManager();
    }

    @ModuleEventHandler
    public void onLogin(UserAuthenticateEvent event){
        Player sender = event.getPlayer().getPlayer();

        GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

        Audience player = pluginService.getPlugin().getBukkitAudiences().player(sender);
        String playerRank = groupManager.getJQGroup(sender);

        JQData jqData = pluginService.getCache().getJQFormats().get(playerRank);

        if (jqData.getAuthFormat() != null) {
            if (jqData.getAuthType() != null) {
                switch (jqData.getAuthType()) {
                    case "none":
                    case "silent":
                        break;
                    case "orderly":
                        if (jqData.authIdIsTheMax(jqData.getAuthFormat().size())) {
                            jqData.clearAuthNextId();
                        }

                        player.sendMessage(TextUtils.convertTextToComponent(sender, jqData.getAuthFormat().get(jqData.getAuthNextId())));
                        jqData.sumAuthNextId();
                    case "random":
                        Random random = new Random();
                        player.sendMessage(TextUtils.convertTextToComponent(sender, jqData.getAuthFormat().get(random.nextInt() * (jqData.getAuthFormat().size() - 1))));

                }

            }
        }

        if (jqData.getAuthMotdList() != null) {
            for (String motdFormat : jqData.getAuthMotdList()) {
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

        if (jqData.getAuthActions() != null) {
            actionManager.execute(sender, jqData.getAuthActions());

        }
    }
}
