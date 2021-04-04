package me.bryangaming.chatlab.managers.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ChatMethod {

    private PluginService pluginService;
    private PlayerMessage playerMethod;
    private ServerData serverData;

    private Configuration command;
    private Configuration config;

    public ChatMethod(PluginService pluginService) {
        this.pluginService = pluginService;

        serverData = pluginService.getServerData();
        this.playerMethod = pluginService.getPlayerMethods().getSender();

        this.config = pluginService.getFiles().getConfig();
        this.command = pluginService.getFiles().getCommand();
    }

    public void clearSubCommand(Player sender, int lines, String world, boolean silent) {

        List<Player> onlinePlayers = new ArrayList<>();
        if (world.equalsIgnoreCase("-global")) {

            onlinePlayers.addAll(Bukkit.getServer().getOnlinePlayers());
            for (Player onlinePlayer : onlinePlayers) {
                for (int times = 0; times < lines; times++) {
                    playerMethod.sendMessage(onlinePlayer, "");
                }

                if (silent) {
                    continue;
                }

                playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.clear.global")
                        .replace("%player%", sender.getName()));
            }

        } else {
            onlinePlayers.addAll(Bukkit.getWorld(world).getPlayers());

            for (Player onlinePlayer : onlinePlayers) {
                for (int times = 0; times < lines; times++) {
                    playerMethod.sendMessage(onlinePlayer, "");
                }

                if (silent) {
                    continue;
                }

                playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.clear.world")
                        .replace("%player%", sender.getName()));
            }

        }

    }

    public void muteSubCommand(Player sender, int seconds, String world, boolean silent) {

        List<Player> onlinePlayers = new ArrayList<>();
        if (world.equalsIgnoreCase("-global")) {

            onlinePlayers.addAll(Bukkit.getServer().getOnlinePlayers());

            serverData.setMuted(true);
            if (seconds == -1) {
                if (silent) {
                    return;
                }

                for (Player onlinePlayer : onlinePlayers) {
                    playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.global.permanent")
                            .replace("%player%", sender.getName()));
                }

                return;
            }


            if (!silent) {
                for (Player onlinePlayer : onlinePlayers) {
                    playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.global.permanent")
                            .replace("%player%", sender.getName()));
                }
            }

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(),
                    new Runnable() {
                        @Override
                        public void run() {
                            serverData.setMuted(false);

                            if (silent) {
                                return;
                            }

                            for (Player onlinePlayer : onlinePlayers) {
                                playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.unmute-temporal"));
                            }
                        }
                    }, 20L * seconds);

        } else {
            onlinePlayers.addAll(Bukkit.getWorld(world).getPlayers());

            serverData.setMuted(true);
            if (seconds == -1) {
                if (silent) {
                    return;
                }

                for (Player onlinePlayer : onlinePlayers) {
                    playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.world.temporal")
                            .replace("%player%", sender.getName())
                            .replace("%time%", String.valueOf(seconds)));
                }
                return;
            }

            if (!silent) {
                for (Player onlinePlayer : onlinePlayers) {
                    playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.global.permanent")
                            .replace("%player%", sender.getName()));
                }
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(),
                    new Runnable() {
                        @Override
                        public void run() {
                            serverData.setMuted(false);

                            if (silent) {
                                return;
                            }

                            for (Player onlinePlayer : onlinePlayers) {
                                playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.unmute-temporal"));
                            }
                        }
                    }, 20L * seconds);

        }
    }

    public void unmuteSubCommand(boolean silent) {

        serverData.setMuted(false);

        if (silent){
            return;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            playerMethod.sendMessage(onlinePlayer, command.getString("commands.chat.mute.unmute-temporal"));
        }
    }


    public void setCooldown(Player player, int time) {

        serverData.setServerTextCooldown(time);
        playerMethod.sendMessage(player, command.getString("commands.chat.cooldown.message")
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
