package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.methods.commands.IgnoreMethod;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UnIgnoreCommand implements CommandClass {


    private final PluginService pluginService;

    public UnIgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;

    }

    @Command(names = "unignore")
    public boolean onCommand(@Sender Player player, @OptArg OfflinePlayer target) {

        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        ModuleCheck moduleCheck = pluginService.getPathManager();
        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();

        ConfigManager files = pluginService.getFiles();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        if (target == null) {
            playerMethod.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("unignore", "<player>")));
            return true;
        }

        if (!(target.isOnline())) {
            playerMethod.sendMessage(player, messages.getString("error.player-offline"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        if (target.getName().equalsIgnoreCase(player.getName())) {
            playerMethod.sendMessage(player, messages.getString("error.ignore.ignore-yourself"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        String targetname = target.getName();
        UUID playeruuid = player.getUniqueId();

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();

        if (!(ignorelist.containsKey(playeruuid))) {
            playerMethod.sendMessage(player, messages.getString("error.ignore.anybody"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");
        UUID targetuuid = target.getPlayer().getUniqueId();

        if (!(ignoredlist.contains(targetname))) {
            playerMethod.sendMessage(player, messages.getString("error.ignore.already-unignored"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        ignoreMethod.unignorePlayer(player, targetuuid);
        playerMethod.sendMessage(player, command.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-unignore");
        return true;
    }
}
