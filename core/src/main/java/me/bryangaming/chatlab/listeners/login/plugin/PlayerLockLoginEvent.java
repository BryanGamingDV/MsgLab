package me.bryangaming.chatlab.listeners.login.plugin;


import eu.locklogin.api.account.AccountID;
import eu.locklogin.api.account.AccountManager;
import eu.locklogin.api.module.plugin.api.event.ModuleEventHandler;
import eu.locklogin.api.module.plugin.api.event.user.AccountCloseEvent;
import eu.locklogin.api.module.plugin.api.event.user.AccountRemovedEvent;
import eu.locklogin.api.module.plugin.api.event.user.UserAuthenticateEvent;
import eu.locklogin.api.module.plugin.api.event.user.UserQuitEvent;
import eu.locklogin.api.module.plugin.api.event.util.EventListener;
import eu.locklogin.api.module.plugin.client.ModulePlayer;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.managers.ActionManager;
	@@ -13,72 +19,111 @@
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLockLoginEvent implements EventListener {

    private final PluginService pluginService;
    private final Configuration formatsFile;
    private final ActionManager actionManager;

    private final static Set<UUID> previously_logged_in = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public PlayerLockLoginEvent(PluginService pluginService) {
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
        this.actionManager = pluginService.getPlayerManager().getActionManager();
    }

    @ModuleEventHandler
    public void onLogin(UserAuthenticateEvent event) {
        if (event.getAuthResult().equals(UserAuthenticateEvent.Result.SUCCESS)) {
            ModulePlayer mp = event.getPlayer();
            Player sender = mp.getPlayer();
            if (!previously_logged_in.contains(mp.getUUID())) {
                if (!formatsFile.getBoolean("join-and-quit.login-hook")){
                    return;
                }

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
    }

    @ModuleEventHandler
    public void onQuit(UserQuitEvent e) {
        previously_logged_in.remove(e.getPlayer().getUUID());
    }

    @ModuleEventHandler
    public void onUnlogin(AccountCloseEvent e) {
        previously_logged_in.remove(e.getPlayer().getUUID());
    }

    @ModuleEventHandler
    public void onRemoved(AccountRemovedEvent e) {
        AccountManager account = e.getRemovedAccount();
        AccountID id = account.getUUID();

        try {
            UUID uuid = UUID.fromString(id.getId());
            Player player = pluginService.getPlugin().getServer().getPlayer(uuid);

            if (player != null) {
                previously_logged_in.remove(player.getUniqueId());
            }
        } catch (Throwable ignored) {}
    }
}
