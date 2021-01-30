package code.commands;

import code.PluginService;
import code.methods.commands.IgnoreMethod;
import code.registry.ConfigManager;
import code.bukkitutils.SoundManager;
import code.utils.Configuration;
import code.methods.player.PlayerMessage;

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

public class UnIgnoreCommand implements CommandClass{


    private final PluginService pluginService;
    public UnIgnoreCommand(PluginService pluginService){
        this.pluginService = pluginService;

    }
    @Command(names = "unignore")
    public boolean onCommand(@Sender Player player, @OptArg OfflinePlayer target) {

        ConfigManager files = pluginService.getFiles();
        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        ModuleCheck moduleCheck = pluginService.getPathManager();
        SoundManager sound = pluginService.getManagingCenter().getSoundManager();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        if (target == null){
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "unignore", "<player>")));
            return true;
        }

        if (!(target.isOnline())){
            playersender.sendMessage(player, messages.getString("error.player-offline"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }
        if (target.getName().equalsIgnoreCase(player.getName())){
            playersender.sendMessage(player, messages.getString("error.ignore.ignore-yourself"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        String targetname = target.getName();
        UUID playeruuid = player.getUniqueId();

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();

        if (!(ignorelist.containsKey(playeruuid))){
            playersender.sendMessage(player, messages.getString("error.ignore.anybody"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");
        UUID targetuuid = target.getPlayer().getUniqueId();

        if (!(ignoredlist.contains(targetname))){
            playersender.sendMessage(player, messages.getString ("error.ignore.already-unignored"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        ignoreMethod.unignorePlayer(player, targetuuid);
        playersender.sendMessage(player, command.getString("commands.ignore.player-unignored")
                    .replace("%player%", targetname));
        sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-unignore");
        return true;
    }
}
