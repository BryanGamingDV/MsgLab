package code.commands;

import code.methods.commands.IgnoreMethod;
import code.registry.ConfigManager;
import code.methods.player.PlayerMessage;
import code.bukkitutils.SoundCreator;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;


import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import code.utils.Configuration;
import code.PluginService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand implements CommandClass{

    private final PluginService pluginService;

    public IgnoreCommand(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @Command(names = "ignore")
    public boolean ignore(@Sender Player sender, @OptArg OfflinePlayer target) {

        ConfigManager files = pluginService.getFiles();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (target == null) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("ignore", "<sender>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        Map<UUID, List<String>> ignorelist = pluginService.getCache().getIgnorelist();
        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (target.getName().equalsIgnoreCase("-list")) {

            if (ignorelist.containsKey(playeruuid) || ignoredlist.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.ignore.anybody"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.ignore.list-ignoredplayers"));
            for (String playersignored : ignoredlist) {
                playerMethod.sendMessage(sender, "&8- &a" + playersignored);
            }

            sound.setSound(playeruuid, "sounds.on-list");
            return true;
        }

        if (!target.isOnline()) {
            playerMethod.sendMessage(sender, messages.getString("error.sender-offline"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }


        if (target.getName().equalsIgnoreCase(sender.getName())) {
            playerMethod.sendMessage(sender, messages.getString("error.ignore.ignore-yourself"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        String targetname = target.getPlayer().getName();
        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();

        if (!ignorelist.containsKey(playeruuid)) {
            ignoreMethod.ignorePlayer(sender, target.getUniqueId());
            playerMethod.sendMessage(sender, command.getString("commands.ignore.sender-ignored")
                    .replace("%sender%", targetname));
            sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-ignore");
            return true;
        }

        if (!(ignoredlist.contains(targetname))) {
            ignoreMethod.ignorePlayer(sender, target.getUniqueId());
            playerMethod.sendMessage(sender, command.getString("commands.ignore.sender-ignored")
                    .replace("%sender%", targetname));
            sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-ignore");
            return true;
        }

        ignoreMethod.unignorePlayer(sender, target.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.ignore.sender-unignored")
                .replace("%sender%", targetname));
        sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-unignore");
        return true;




    }


}
