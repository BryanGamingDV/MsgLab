package atogesputo.bryangaming.chatlab.listeners;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.data.UserData;
import atogesputo.bryangaming.chatlab.managers.GroupMethod;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import atogesputo.bryangaming.chatlab.events.server.ChangeMode;
import atogesputo.bryangaming.chatlab.events.server.ServerChangeEvent;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import atogesputo.bryangaming.chatlab.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
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
        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();

        Player you = event.getPlayer();
        UserData playerStatus = pluginService.getCache().getUserDatas().get(you.getUniqueId());

        String playerRank = groupMethod.getJQGroup(you);

        if (moduleCheck.isOptionEnabled("join_quit")) {
            Bukkit.getPluginManager().callEvent(new ServerChangeEvent(event, event.getPlayer(), playerRank, ChangeMode.QUIT));
        }

        if (moduleCheck.isCommandEnabled("reply")) {
            if (!playerStatus.hasRepliedPlayer()) {
                return;
            }

            UserData target = pluginService.getCache().getUserDatas().get(playerStatus.getRepliedPlayer());

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
