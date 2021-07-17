package me.bryangaming.chatlab.listeners.login;

import eu.locklogin.api.util.platform.CurrentPlatform;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.events.PlayerLoginEvent;
import me.bryangaming.chatlab.managers.ActionManager;
import me.bryangaming.chatlab.managers.group.GroupManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.text.TextUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class PlayerLoginListener implements Listener {

    private final PluginService pluginService;
    private final Configuration formatsFile;
    private final ActionManager actionManager;

    public PlayerLoginListener(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();

        this.actionManager = pluginService.getPlayerManager().getActionManager();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        if (!formatsFile.getBoolean("join-and-quit.login-hook")){
            return;
        }

        if (!pluginService.getPlugin().getServer().getPluginManager().isPluginEnabled(JavaPlugin.getProvidingPlugin(CurrentPlatform.getMain()))) {
            GroupManager groupManager = pluginService.getPlayerManager().getGroupManager();

            Player sender = event.getPlayer();

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
}
