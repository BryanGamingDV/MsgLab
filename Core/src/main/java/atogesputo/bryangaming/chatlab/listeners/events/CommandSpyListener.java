package atogesputo.bryangaming.chatlab.listeners.events;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.data.UserData;
import atogesputo.bryangaming.chatlab.events.CommandSpyEvent;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import atogesputo.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class CommandSpyListener implements Listener {

    private PluginService pluginService;

    public CommandSpyListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onCommandSpy(CommandSpyEvent commandSpyEvent) {

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        Configuration command = pluginService.getFiles().getCommand();

        List<String> blockedCommands = command.getStringList("commands.commandspy.blocked-commands");

        for (String blockedCommand : blockedCommands){
            if (blockedCommand.startsWith(commandSpyEvent.getMessage())){
                return;
            }
        }

        String commandSpyFormat = command.getString("commands.commandspy.format")
                .replace("%sender%", commandSpyEvent.getSender())
                .replace("%command%", "/" + commandSpyEvent.getMessage());

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            UserData watcherSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

            if (!watcherSpy.isCommandspyMode()) {
                return;
            }

            playersender.sendMessage(player, commandSpyFormat);
            playersender.sendSound(player, SoundEnum.RECEIVE_COMMANDSPY);
        });
    }
}
