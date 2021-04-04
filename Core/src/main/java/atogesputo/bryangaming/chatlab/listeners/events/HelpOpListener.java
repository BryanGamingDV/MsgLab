package atogesputo.bryangaming.chatlab.listeners.events;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.data.UserData;
import atogesputo.bryangaming.chatlab.events.HelpOpEvent;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import atogesputo.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
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
