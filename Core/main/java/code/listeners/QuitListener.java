package code.listeners;

import code.PluginService;
import code.data.UserData;
import code.methods.ListenerManaging;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private final PluginService pluginService;

    public QuitListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        PlayerMessage player = pluginService.getPlayerMethods().getSender();
        Configuration command = pluginService.getFiles().getCommand();

        ModuleCheck moduleCheck = pluginService.getPathManager();
        ListenerManaging listenerManaging = pluginService.getPlayerMethods().getListenerManaging();

        Player you = event.getPlayer();
        UserData playerStatus = pluginService.getCache().getPlayerUUID().get(you.getUniqueId());

        if (moduleCheck.isOptionEnabled("join_quit")) {
            listenerManaging.setQuit(event);
        }

        if (moduleCheck.isCommandEnabled("reply")) {
            if (!playerStatus.hasRepliedPlayer()) {
                return;
            }

            UserData target = pluginService.getCache().getPlayerUUID().get(playerStatus.getRepliedPlayer());

            if (!target.hasRepliedPlayer()) {
                return;
            }

            if (target.hasRepliedPlayer(you.getUniqueId())) {
                target.setRepliedPlayer(null);
            }

            player.sendMessage(target.getPlayer(), command.getString("commands.msg-toggle.left")
                    .replace("%player%", target.getPlayer().getName())
                    .replace("%arg-1%", event.getPlayer().getName()));
        }
        playerStatus.resetStats();

    }

}
