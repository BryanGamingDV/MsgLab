package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.commands.ChatManager;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;


@Command(names = {"chat"})
public class ChatCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration command;
    private final Configuration messages;
    private final Configuration utils;

    private final ServerData serverData;

    private final ChatManager chatManager;
    private final SenderManager playerMethod;


    public ChatCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.serverData = pluginService.getServerData();

        this.chatManager = pluginService.getPlayerManager().getChatMethod();
        this.playerMethod = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean mainSubCommand(@Sender Player sender) {
        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("chat", "help, reload, clear, mute, unmute, cooldown, color")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        command.getStringList("commands.chat.help")
                .forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat help");
        return true;

    }

    @Command(names = "clear")
    public boolean clearSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        int lines = command.getInt("commands.chat.clear.default-blank");
        boolean silent = false;

        if (text.isEmpty()) {
            chatManager.clearSubCommand(sender, lines, world, silent);
            return true;
        }

        for (int id = 0; id < strings.length; id++) {

            if (strings[id].equalsIgnoreCase("-silent")) {
                silent = true;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-w")) {

                if (strings[id + 1].isEmpty()) {
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")) {
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-world")
                            .replace("%world%", strings[id + 1]));
                    return true;
                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-l")) {

                try {
                    lines = Integer.parseInt(strings[id + 1]);
                } catch (NumberFormatException numberFormatException) {
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-number")
                            .replace("%world%", strings[id + 1]));

                    return true;
                }
                id++;
                continue;
            }

            playerMethod.sendSound(sender, SoundEnum.ERROR);
            playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-flag")
                    .replace("%flag%", strings[id]));
            break;
        }

        chatManager.clearSubCommand(sender, lines, world, silent);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat clear");
        return true;
    }

    @Command(names = "mute")
    public boolean muteSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        String channel = "-none";

        int times = -1;
        boolean silent = false;

        if (text.isEmpty()) {
            chatManager.muteSubCommand(sender, times, channel, world, false);
            return true;
        }


        for (int id = 0; id < strings.length; id++) {
            if (strings[id].equalsIgnoreCase("-silent")) {
                silent = true;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-w")) {
                if (strings[id + 1].isEmpty()) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-world")
                            .replace("%world%", strings[id + 1]));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;

                }

                if (strings[id + 1].equalsIgnoreCase("-global")) {
                    if (serverData.isMuted()) {
                        playerMethod.sendMessage(sender, messages.getString("error.chat.management.already-muted"));
                        playerMethod.sendSound(sender, SoundEnum.ERROR);
                        return true;
                    }
                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-c")) {
                if (strings[id + 1].isEmpty()) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (utils.getString("channel." + channel) == null && !strings[id + 1].equalsIgnoreCase("-none")) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-channel")
                            .replace("%channel%", text));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                channel = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-t")) {

                if (strings[id + 1].isEmpty()) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                try {
                    times = Integer.parseInt(strings[id + 1]);
                } catch (NumberFormatException numberFormatException) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-number")
                            .replace("%seconds%", strings[id + 1]));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                id++;
                continue;
            }

            playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-flag")
                    .replace("%flag%", strings[id]));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        chatManager.muteSubCommand(sender, times, channel, world, silent);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat mute");
        return true;

    }

    @Command(names = "unmute")
    public boolean unmuteSubCommand(@Sender Player sender, @OptArg("") @Text String args) {

        String world = "-global";
        String channel = "-none";

        boolean silent = false;

        if (args.isEmpty()){
            chatManager.unmuteSubCommand(world, channel, silent);
            return true;
        }
        String[] strings = args.split(" ");

        for (int id = 0; id < strings.length; id++) {

            if (strings[id].equalsIgnoreCase("-silent")) {
                silent = true;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-w")) {
                if (strings[id + 1].isEmpty()) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-world")
                            .replace("%world%", strings[id + 1]));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;

                }

                if (strings[id + 1].equalsIgnoreCase("-global")) {
                    if (!serverData.isMuted()) {
                        playerMethod.sendMessage(sender, messages.getString("error.chat.management.already-unmuted"));
                        playerMethod.sendSound(sender, SoundEnum.ERROR);
                        return true;
                    }
                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-c")) {
                if (strings[id + 1].isEmpty()) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (utils.getString("channel." + channel) == null && !strings[id + 1].equalsIgnoreCase("-none")) {
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-channel")
                            .replace("%channel%", strings[id + 1]));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                channel = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-silent")) {
                silent = true;
                continue;
            }

            playerMethod.sendMessage(sender, messages.getString("chat.flags.unknown-flag")
                    .replace("%flag%", args));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        chatManager.unmuteSubCommand(world, channel, silent);
        return true;
    }


    @Command(names = "cooldown")
    public boolean cooldownSubCommand(@Sender Player sender, @OptArg("") String text) {

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("chat", "cooldown", "time")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.equalsIgnoreCase("-d")) {
            chatManager.setCooldown(sender, utils.getInt("fitlers.cooldown.text.seconds"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
            return true;
        }

        int time;

        try {
            time = Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-number")
                    .replace("%text%", text));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        chatManager.setCooldown(sender, time);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
        return true;

    }

    @Command(names = "color")
    public boolean colorSubCommand(@Sender Player player, @OptArg("") String tag, @OptArg("") String color) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (tag.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.empty-tags")
                    .replace("%tags%", chatManager.allTags())
                    .replace("%usage%", TextUtils.getUsage("chat", "color", "[<typetag>]", "[@tag/color]")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!chatManager.isTag(tag)) {
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.unknown-tag")
                    .replace("%text%", tag));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        HashMap<String, String> tagData = userData.gethashTags();

        if (color.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.empty-colortags")
                    .replace("%colortags%", chatManager.allColorTags())
                    .replace("%usage%", TextUtils.getUsage("chat", "color", "typetag", "[@tag/color]")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (color.startsWith("@")) {
            if (command.getString("commands.chat.color.tags." + color.substring(1)) == null) {
                playerMethod.sendMessage(player, messages.getString("error.chat.tags.unknown-tagcolor")
                        .replace("%text%", color));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            if (tagData.containsKey(tag)) {
                tagData.replace(tag, color.substring(1));
            } else {
                tagData.put(tag, color.substring(1));
            }

            playerMethod.sendMessage(player, command.getString("commands.chat.color.selected.tag")
                    .replace("%tag%", color.substring(1)));
            playerMethod.sendSound(player, SoundEnum.ARGUMENT, "chat color");
            return true;
        }

        if (tagData.containsKey(tag)) {
            tagData.replace(tag, color);
        } else {
            tagData.put(tag, color);
        }
        playerMethod.sendMessage(player, command.getString("commands.chat.color.selected.color")
                .replace("%color%", color.replace("&", "&_")));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "chat color");
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender) {

        if (!playerMethod.hasPermission(sender, "commands.chat.reload")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;

        }

        utils.reload();
        playerMethod.sendMessage(sender, command.getString("commands.chat.reload"));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat reload");
        return true;
    }

}
