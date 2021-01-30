package code.commands;

import code.CacheManager;
import code.methods.commands.IgnoreMethod;
import code.registry.ConfigManager;
import code.methods.player.PlayerMessage;
import code.bukkitutils.SoundManager;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;


import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import code.utils.Configuration;
import code.Manager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand implements CommandClass{

    private final Manager manager;

    public IgnoreCommand(Manager manager){
        this.manager = manager;
    }

    @Command(names = "ignore")
    public boolean ignore(@Sender Player player, @OptArg OfflinePlayer target) {

        ConfigManager files = manager.getFiles();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        SoundManager sound = manager.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = manager.getPathManager();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (target == null) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("ignore", "<player>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        Map<UUID, List<String>> ignorelist = manager.getCache().getIgnorelist();
        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (target.getName().equalsIgnoreCase("-list")) {

            if (ignorelist.containsKey(playeruuid) || ignoredlist.isEmpty()) {
                playersender.sendMessage(player, messages.getString("error.ignore.anybody"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            playersender.sendMessage(player, command.getString("commands.ignore.list-ignoredplayers"));
            for (String playersignored : ignoredlist) {
                playersender.sendMessage(player, "&8- &a" + playersignored);
            }
            sound.setSound(playeruuid, "sounds.on-list");
            return true;
        }

        if (!target.isOnline()) {
            playersender.sendMessage(player, messages.getString("error.player-offline"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }


        if (target.getName().equalsIgnoreCase(player.getName())) {
            playersender.sendMessage(player, messages.getString("error.ignore.ignore-yourself"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        String targetname = target.getPlayer().getName();
        IgnoreMethod ignoreMethod = manager.getPlayerMethods().getIgnoreMethod();

        if (!ignorelist.containsKey(playeruuid)) {
            ignoreMethod.ignorePlayer(player, target.getUniqueId());
            playersender.sendMessage(player, command.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-ignore");
            return true;
        }

        if (!(ignoredlist.contains(targetname))) {
            ignoreMethod.ignorePlayer(player, target.getUniqueId());
            playersender.sendMessage(player, command.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-ignore");
            return true;
        }

        ignoreMethod.unignorePlayer(player, target.getUniqueId());
        playersender.sendMessage(player, command.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        sound.setSound(target.getPlayer().getUniqueId(), "sounds.on-unignore");
        return true;




    }


}
