package code.methods.commands;

import code.Manager;
import code.CacheManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreMethod{

    private final Configuration players;
    private final Configuration messages;

    private final Manager manager;

    private final CacheManager cache;

    private final Map<UUID, List<String>> ignorelist;



    public IgnoreMethod(Manager manager) {
        this.manager = manager;

        this.cache = manager.getCache();
        this.ignorelist = cache.getIgnorelist();
        this.players = manager.getFiles().getPlayers();
        this.messages = manager.getFiles().getMessages();

    }


    public void ignorePlayer(CommandSender sender, UUID uuid) {

        Player you = (Player) sender;
        UUID playeruuid = you.getUniqueId();

        OfflinePlayer player = Bukkit.getPlayer(uuid);
        List<String> ignoredPlayers;

        if (ignorelist.get(playeruuid) == null) {
            ignoredPlayers = new ArrayList<>();
        } else {
            ignoredPlayers = ignorelist.get(playeruuid);
        }

        ignoredPlayers.add(player.getName());
        ignorelist.put(playeruuid, ignoredPlayers);

        players.set("players." + playeruuid + ".name", you.getName());
        players.set("players." + playeruuid + ".players-ignored", ignoredPlayers);

        players.save();


    }

    public void unignorePlayer(CommandSender sender, UUID uuid){

        Player you = (Player) sender;
        UUID playeruuid = you.getUniqueId();

        OfflinePlayer target = Bukkit.getPlayer(uuid);

        List<String> ignoredPlayers = ignorelist.get(playeruuid);
        ignoredPlayers.remove(target.getName());
        players.set("players." + playeruuid + ".players-ignored", ignoredPlayers);
        players.save();

        if (players.getStringList("players." + playeruuid + ".players-ignored").isEmpty()){
            players.set("players." + uuid, null);
            players.save();
        }

    }

    public boolean playerIsIgnored(UUID sender, UUID playerIgnored) {

        Configuration players = manager.getFiles().getPlayers();
        ModuleCheck moduleCheck = manager.getPathManager();

        String playerName = Bukkit.getPlayer(playerIgnored).getName();

        if (moduleCheck.isCommandEnabled("ignore")) {

            if (!(players.contains("players"))) return false;

            List<String> ignorelist = players
                    .getStringList("players." + sender.toString() + ".players-ignored");

            return ignorelist.contains(playerName);
        }
        return false;
    }
}
