package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.ChatManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
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

    private final Configuration commandFile;
    private final Configuration messagesFile;
    private final Configuration formatFile;

    private final ServerData serverData;

    private final ChatManager chatManager;
    private final SenderManager senderManager;


    public ChatCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
        this.formatFile = pluginService.getFiles().getFormatsFile();
        this.serverData = pluginService.getServerData();

        this.chatManager = pluginService.getPlayerManager().getChatMethod();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean mainSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("chat", "help, reload, clear, mute, unmute, cooldown, color")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        commandFile.getStringList("commands.chat.help")
                .forEach(text -> senderManager.sendMessage(sender, text));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat help");
        return true;

    }

    @Command(names = "clear")
    public boolean clearSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        int lines = commandFile.getInt("commands.chat.clear.default-blank");
        boolean silent = false;

        if (text.isEmpty()) {
            chatManager.clearSubCommand(sender, lines, world, false);
            return true;
        }

        for (int id = 0; id < strings.length; id++) {

            if (strings[id].equalsIgnoreCase("-silent")) {
                silent = true;
                continue;
            }
            if (strings[id].equalsIgnoreCase("-w")) {

                if (strings[id + 1].isEmpty()) {
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.empty-flag"));
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")) {
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-world")
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
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-number")
                            .replace("%world%", strings[id + 1]));

                    return true;
                }
                id++;
                continue;
            }

            senderManager.playSound(sender, SoundEnum.ERROR);
            senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-flag")
                    .replace("%flag%", strings[id]));
            break;
        }

        chatManager.clearSubCommand(sender, lines, world, silent);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat clear");
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
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.empty-flag"));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-world")
                            .replace("%world%", strings[id + 1]));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;

                }

                if (strings[id + 1].equalsIgnoreCase("-global")) {
                    if (serverData.isMuted()) {
                        senderManager.sendMessage(sender, messagesFile.getString("error.chat.management.already-muted"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }
                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-c")) {
                if (strings[id + 1].isEmpty()) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.empty-flag"));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (formatFile.getString("channel." + channel) == null && !strings[id + 1].equalsIgnoreCase("-none")) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-channel")
                            .replace("%channel%", text));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                channel = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-t")) {

                if (strings[id + 1].isEmpty()) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.empty-flag"));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                try {
                    times = Integer.parseInt(strings[id + 1]);
                } catch (NumberFormatException numberFormatException) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-number")
                            .replace("%seconds%", strings[id + 1]));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                id++;
                continue;
            }

            senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-flag")
                    .replace("%flag%", strings[id]));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        chatManager.muteSubCommand(sender, times, channel, world, silent);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat mute");
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
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.empty-flag"));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-world")
                            .replace("%world%", strings[id + 1]));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;

                }

                if (strings[id + 1].equalsIgnoreCase("-global")) {
                    if (!serverData.isMuted()) {
                        senderManager.sendMessage(sender, messagesFile.getString("error.chat.management.already-unmuted"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }
                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-c")) {
                if (strings[id + 1].isEmpty()) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.empty-flag"));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
                }

                if (formatFile.getString("channel." + channel) == null && !strings[id + 1].equalsIgnoreCase("-none")) {
                    senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-channel")
                            .replace("%channel%", strings[id + 1]));
                    senderManager.playSound(sender, SoundEnum.ERROR);
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

            senderManager.sendMessage(sender, messagesFile.getString("chat.flags.unknown-flag")
                    .replace("%flag%", args));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        chatManager.unmuteSubCommand(world, channel, silent);
        return true;
    }


    @Command(names = "cooldown")
    public boolean cooldownSubCommand(@Sender Player sender, @OptArg("") String text) {

        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("chat", "cooldown", "time")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.equalsIgnoreCase("-d")) {
            chatManager.setCooldown(sender, formatFile.getInt("filters.cooldown.text.seconds"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
            return true;
        }

        if (TextUtils.isNumber(text)){
            senderManager.sendMessage(sender, messagesFile.getString("error.flags.unknown-number")
                    .replace("%text%", text));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }
        int time = Integer.parseInt(text);

        chatManager.setCooldown(sender, time);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
        return true;

    }

    @Command(names = "color")
    public boolean colorSubCommand(@Sender Player player, @OptArg("") String tag, @OptArg("") String color) {

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (tag.isEmpty()) {
            senderManager.sendMessage(player, messagesFile.getString("error.chat.tags.empty-tags")
                    .replace("%tags%", chatManager.allTags())
                    .replace("%usage%", TextUtils.getUsage("chat", "color", "[<typetag>]", "[@tag/color]")));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!chatManager.isTag(tag)) {
            senderManager.sendMessage(player, messagesFile.getString("error.chat.tags.unknown-tag")
                    .replace("%text%", tag));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        HashMap<String, String> tagData = userData.gethashTags();

        if (color.isEmpty()) {
            senderManager.sendMessage(player, messagesFile.getString("error.chat.tags.empty-colortags")
                    .replace("%colortags%", chatManager.allColorTags())
                    .replace("%usage%", TextUtils.getUsage("chat", "color", "typetag", "[@tag/color]")));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (color.startsWith("@")) {
            if (commandFile.getString("commands.chat.color.tags." + color.substring(1)) == null) {
                senderManager.sendMessage(player, messagesFile.getString("error.chat.tags.unknown-tagcolor")
                        .replace("%text%", color));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            if (tagData.containsKey(tag)) {
                tagData.replace(tag, color.substring(1));
            } else {
                tagData.put(tag, color.substring(1));
            }

            senderManager.sendMessage(player, commandFile.getString("commands.chat.color.selected.tag")
                    .replace("%tag%", color.substring(1)));
            senderManager.playSound(player, SoundEnum.ARGUMENT, "chat color");
            return true;
        }

        if (tagData.containsKey(tag)) {
            tagData.replace(tag, color);
        } else {
            tagData.put(tag, color);
        }
        senderManager.sendMessage(player, commandFile.getString("commands.chat.color.selected.color")
                .replace("%color%", color.replace("&", "&_")));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "chat color");
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender) {

        if (!senderManager.hasPermission(sender, "commands.chat.reload")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;

        }

        formatFile.reload();
        senderManager.sendMessage(sender, commandFile.getString("commands.chat.reload"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat reload");
        return true;
    }

}
