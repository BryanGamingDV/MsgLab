package code.listeners.events;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.data.UserData;
import code.events.HelpOpEvent;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HelpOpListener implements Listener {


    private PluginService pluginService;

    public HelpOpListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    @EventHandler
    public void onCommandSpy(HelpOpEvent helpopEvent) {

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        Configuration command = pluginService.getFiles().getCommand();

        Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (!playerMethod.hasPermission(onlinePlayer, "commands.helpop.watch") || !onlineCache.isPlayerHelpOp()) {
                return;
            }

            playerMethod.sendMessage(onlinePlayer, command.getString("commands.helpop.message")
                    .replace("%player%", onlinePlayer.getName())
                    .replace("%message%", helpopEvent.getMessage()));
            playerMethod.sendSound(onlinePlayer, SoundEnum.RECEIVE_HELPOP);
        });
    }
}
