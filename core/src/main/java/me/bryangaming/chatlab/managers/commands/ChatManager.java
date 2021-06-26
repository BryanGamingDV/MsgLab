package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatManager {

    private final PluginService pluginService;
    private final SenderManager senderManager;
    private final ServerData serverData;

    private final Configuration messagesFile;
    private final Configuration filtersFile;

    public ChatManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messagesFile = pluginService.getFiles().getMessagesFile();
        this.filtersFile = pluginService.getFiles().getFiltersFile();

        this.serverData = pluginService.getServerData();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    public void clearSubCommand(Player sender, int lines, String world, boolean silent) {

        List<Player> onlinePlayers = new ArrayList<>();
        if (world.equalsIgnoreCase("-global")) {

            onlinePlayers.addAll(Bukkit.getServer().getOnlinePlayers());
            for (Player onlinePlayer : onlinePlayers) {
                for (int times = 0; times < lines; times++) {
                    senderManager.sendMessage(onlinePlayer, "");
                }

                if (silent) {
                    continue;
                }

                senderManager.sendMessage(onlinePlayer, messagesFile.getString("chat.clear.global")
                        .replace("%player%", sender.getName()));
            }

        } else {
            onlinePlayers.addAll(Bukkit.getWorld(world).getPlayers());

            for (Player onlinePlayer : onlinePlayers) {
                for (int times = 0; times < lines; times++) {
                    senderManager.sendMessage(onlinePlayer, "");
                }

                if (silent) {
                    continue;
                }

                senderManager.sendMessage(onlinePlayer, messagesFile.getString("chat.clear.world")
                        .replace("%player%", sender.getName()));
            }

        }

    }

    public void muteSubCommand(Player sender, int seconds, String channelPath, String worldPath, boolean silent) {

        List<Player> onlinePlayers = new ArrayList<>();
        List<Player> channelPlayerList = new ArrayList<>();

        if (!channelPath.equalsIgnoreCase("-global")) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                UserData onlineData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

                if (!onlineData.equalsChannelGroup(channelPath)) {
                    break;
                }

                channelPlayerList.add(onlinePlayer);
            }
        }

        if (worldPath.equalsIgnoreCase("-global")) {
            onlinePlayers.addAll(Bukkit.getServer().getOnlinePlayers());
            serverData.setMuted(true);
        } else {
            World world = Bukkit.getWorld(worldPath);
            onlinePlayers.addAll(world.getPlayers());
            serverData.muteWorld(world);
        }


        if (!silent) {
            if (!channelPath.equalsIgnoreCase("-none")) {

                if (seconds != -1) {
                    senderManager.sendMessageTo(channelPlayerList, messagesFile.getString("chat.mute.channel.permanent")
                            .replace("%player%", sender.getName()));
                } else {
                    senderManager.sendMessageTo(channelPlayerList, messagesFile.getString("chat.mute.channel.permanent")
                            .replace("%player%", sender.getName())
                            .replace("%time%", String.valueOf(seconds)));
                }
            }

            if (worldPath.equalsIgnoreCase("-global")) {
                if (seconds != -1) {
                    senderManager.sendMessageTo(onlinePlayers, messagesFile.getString("chat.mute.global.permanent")
                            .replace("%player%", sender.getName()));
                } else {
                    senderManager.sendMessageTo(onlinePlayers, messagesFile.getString("chat.mute.global.permanent")
                            .replace("%player%", sender.getName())
                            .replace("%time%", String.valueOf(seconds)));
                }
            }else{
                if (seconds != -1) {
                    senderManager.sendMessageTo(onlinePlayers, messagesFile.getString("chat.mute.world.permanent")
                            .replace("%player%", sender.getName()));
                }else{
                    senderManager.sendMessageTo(onlinePlayers, messagesFile.getString("chat.mute.world.permanent")
                            .replace("%player%", sender.getName())
                            .replace("%time%", String.valueOf(seconds)));
                }
            }
        }

        if (seconds != -1) {
            return;
        }


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (serverData.isMuted()) {
                    serverData.setMuted(false);
                }

                if (!worldPath.equalsIgnoreCase("-global")){
                    World world = Bukkit.getWorld(worldPath);

                    if (serverData.isWorldMuted(world)){
                        serverData.unmuteWorld(world);
                    }
                }

                if (!channelPath.equalsIgnoreCase("-none")){
                    if (serverData.isChannelMuted(worldPath)){
                        serverData.unmuteChannel(channelPath);
                    }
                }
                if (!channelPath.equalsIgnoreCase("-none")) {
                    senderManager.sendMessageTo(channelPlayerList, messagesFile.getString("chat.unmute.temporal"));
                }else{
                    senderManager.sendMessageTo(onlinePlayers, messagesFile.getString("chat.unmute.temporal"));

                }
            }
        }, 20L * seconds);
    }

    public void unmuteSubCommand(String world, String channel, boolean silent) {

        if (!channel.equalsIgnoreCase("-none")){
            serverData.unmuteChannel(channel);
        }

        if (!world.equalsIgnoreCase("-global")){
            serverData.unmuteWorld(Bukkit.getWorld(world));
        }else {
            serverData.setMuted(false);
        }

        if (silent) {
            return;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            senderManager.sendMessage(onlinePlayer, messagesFile.getString("chat.unmute.permanent")
                    .replace("%player%", onlinePlayer.getName()));
        }
    }


    public void setCooldown(Player player, int time) {

        serverData.setServerTextCooldown(time);
        senderManager.sendMessage(player, messagesFile.getString("chat.cooldown.message")
                .replace("%time%", String.valueOf(time)));
    }


    public Set<String> checkTags() {
        return filtersFile.getConfigurationSection("tags").getKeys(false);
    }

    public Set<String> checkColorTags() {
        return messagesFile.getConfigurationSection("chat.color.tags").getKeys(false);
    }

}
