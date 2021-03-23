package code.listeners.events;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.data.UserData;
import code.events.CommandSpyEvent;
import code.events.SocialSpyEvent;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
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
            UserData watcherSpy = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

            if (!watcherSpy.isCommandspyMode()) {
                return;
            }

            playersender.sendMessage(player, commandSpyFormat);
            playersender.sendSound(player, SoundEnum.RECEIVE_COMMANDSPY);
        });
    }
}
