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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ChatManager {

    private PluginService pluginService;
    private final SenderManager senderManager;
    private ServerData serverData;

    private Configuration command;
    private Configuration config;

    public ChatManager(PluginService pluginService) {
        this.pluginService = pluginService;

        this.serverData = pluginService.getServerData();
        this.senderManager = pluginService.getPlayerManager().getSender();
        this.config = pluginService.getFiles().getConfigFile();
        this.command = pluginService.getFiles().getCommandFile();
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

                senderManager.sendMessage(onlinePlayer, command.getString("commands.chat.clear.global")
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

                senderManager.sendMessage(onlinePlayer, command.getString("commands.chat.clear.world")
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
                    senderManager.sendMessageTo(channelPlayerList, command.getString("commands.chat.mute.channel.permanent")
                            .replace("%player%", sender.getName()));
                } else {
                    senderManager.sendMessageTo(channelPlayerList, command.getString("commands.chat.mute.channel.permanent")
                            .replace("%player%", sender.getName())
                            .replace("%time%", String.valueOf(seconds)));
                }
            }

            if (worldPath.equalsIgnoreCase("-global")) {
                if (seconds != -1) {
                    senderManager.sendMessageTo(onlinePlayers, command.getString("commands.chat.mute.global.permanent")
                            .replace("%player%", sender.getName()));
                } else {
                    senderManager.sendMessageTo(onlinePlayers, command.getString("commands.chat.mute.global.permanent")
                            .replace("%player%", sender.getName())
                            .replace("%time%", String.valueOf(seconds)));
                }
            }else{
                if (seconds != -1) {
                    senderManager.sendMessageTo(onlinePlayers, command.getString("commands.chat.mute.world.permanent")
                            .replace("%player%", sender.getName()));
                }else{
                    senderManager.sendMessageTo(onlinePlayers, command.getString("commands.chat.mute.world.permanent")
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

                if (silent) {
                    return;
                }

                for (Player onlinePlayer : onlinePlayers) {
                    senderManager.sendMessage(onlinePlayer, command.getString("commands.chat.mute.unmute-temporal"));
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
            senderManager.sendMessage(onlinePlayer, command.getString("commands.chat.mute.unmute-temporal"));
        }
    }


    public void setCooldown(Player player, int time) {

        serverData.setServerTextCooldown(time);
        senderManager.sendMessage(player, command.getString("commands.chat.cooldown.message")
                .replace("%time%", String.valueOf(time)));
    }


    public Set<String> checkTags() {
        return config.getConfigurationSection("options.color").getKeys(false);
    }

    public Set<String> checkColorTags() {
        return command.getConfigurationSection("commands.chat.color.tags").getKeys(false);
    }

    public boolean isTag(String tag) {
        for (String tags : checkTags()) {
            if (tags.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }


    public String allTags() {
        return String.join(", ", checkTags());
    }

    public String allColorTags() {
        return String.join(",", checkColorTags());
    }

    public String replaceTagsVariables(Player player, String message) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        HashMap<String, String> hashmapTag = userData.gethashTags();

        for (String tag : checkTags()) {

            String variableTag = config.getString("options.color." + tag + ".variable");

            if (variableTag == null) {
                System.out.println("Nulltag " + tag);
                continue;
            }


            if (!message.contains(variableTag)) {
                continue;
            }

            if (!player.hasPermission(variableTag)) {
                message = message.replace(variableTag, "");
                continue;
            }


            if (!hashmapTag.containsKey(tag)) {
                message = message.replace(variableTag, "");
                continue;
            }

            message = message.replace(variableTag, hashmapTag.get(tag));

        }

        return message;

    }
}
