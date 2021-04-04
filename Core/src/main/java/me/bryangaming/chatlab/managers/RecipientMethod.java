package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.bukkitutils.WorldData;
import me.bryangaming.chatlab.managers.chat.RadialChatMethod;
import me.bryangaming.chatlab.managers.commands.IgnoreMethod;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipientMethod {

    private PluginService pluginService;

    public RecipientMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public List<Player> getRecipients(AsyncPlayerChatEvent event) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        Player player = event.getPlayer();

        UserData playerData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();

        List<Player> playerList = null;
        event.getRecipients().clear();

        if (utils.getBoolean("per-world-chat.enabled")) {
            if (utils.getBoolean("per-world-chat.all-worlds")) {
                for (String worldname : WorldData.getAllWorldChat()) {
                    if (player.getWorld().getName().equalsIgnoreCase(worldname)) {
                        World world = Bukkit.getWorld(worldname);
                        playerList = new ArrayList<>(world.getPlayers());

                    }
                }

            } else {
                playerList = new ArrayList<>();
                for (String worldname : WorldData.getWorldChat(player)) {
                    World world = Bukkit.getWorld(worldname);
                    playerList.addAll(world.getPlayers());
                }
            }
        } else {
            playerList = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        }

        if (playerList == null) {
            pluginService.getPlugin().getLogger().info("How did you get here?" +
                    utils.getBoolean("per-world-chat.enabled"));
            return null;
        }

        RadialChatMethod radialChatMethod = pluginService.getPlayerMethods().getRadialChatMethod();

        if (utils.getBoolean("radial-chat.enabled")) {

            Iterator<Player> playerRadialIterator = playerList.iterator();
            List<Player> radialPlayerList = radialChatMethod.getRadialPlayers(player);

            while (playerRadialIterator.hasNext()) {
                Player playerRadial = playerRadialIterator.next();

                if (radialPlayerList.contains(playerRadial)) {
                    continue;
                }

                radialPlayerList.remove(player);
            }
        }

        Iterator<Player> playerIterator = playerList.iterator();
        while (playerIterator.hasNext()) {
            Player playerChannel = playerIterator.next();
            UserData playerCache = pluginService.getCache().getUserDatas().get(playerChannel.getUniqueId());

            if (!playerCache.equalsChannelGroup(playerData.getChannelGroup())) {
                playerIterator.remove();
            }

            if (ignoreMethod.playerIsIgnored(playerChannel.getUniqueId(), player.getUniqueId())) {
                playerIterator.remove();

            }
        }


        return playerList;
    }
}
