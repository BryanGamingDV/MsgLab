package code.commands;

import code.MsgLab;
import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.bukkitutils.sound.SoundManager;
import code.data.UserData;
import code.managers.commands.ChatMethod;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.StringFormat;
import code.utils.module.ModuleCheck;
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
    private final MsgLab plugin;

    private final Configuration command;
    private final Configuration messages;
    private final Configuration utils;

    private final ModuleCheck moduleCheck;

    private final ChatMethod chatMethod;
    private final PlayerMessage playerMethod;

    private final SoundManager sound;


    public ChatCommand(MsgLab plugin, PluginService pluginService) {
        this.pluginService = pluginService;
        this.plugin = plugin;

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.moduleCheck = pluginService.getPathManager();

        this.chatMethod = pluginService.getPlayerMethods().getChatMethod();
        this.playerMethod = pluginService.getPlayerMethods().getSender();

        this.sound = pluginService.getManagingCenter().getSoundManager();
    }

    @Command(names = "")
    public boolean mainSubCommand(@Sender Player sender) {
        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("chat", "help, reload, clear, mute, unmute, cooldown, color")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        StringFormat variable = pluginService.getStringFormat();

        variable.loopString(sender, command, "commands.chat.help");
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat help");
        return true;

    }

    @Command(names = "clear")
    public boolean clearSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        int lines = command.getInt("commands.chat.clear.default-blank");
        boolean silent = false;

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
            playerMethod.sendMessage(sender, messages.getString("chat.flags.unknown-flag")
                    .replace("%flag%", strings[id]));
            break;
        }

        chatMethod.clearSubCommand(sender, lines, world, silent);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat clear");
        return true;
    }

    @Command(names = "mute")
    public boolean muteSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        int times = -1;
        boolean silent = false;

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
                            .replace("%world%", text));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;

                }

                world = strings[id + 1];
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
                            .replace("%world%", strings[id + 1]));
                    playerMethod.sendSound(sender, SoundEnum.ERROR);
                    return true;
                }

                id++;
                continue;
            }

            playerMethod.sendMessage(sender, messages.getString("chat.flags.unknown-flag")
                    .replace("%flag%", strings[id]));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            break;
        }

        chatMethod.muteSubCommand(sender, times, world, silent);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat mute");
        return true;

    }

    @Command(names = "unmute")
    public boolean unmuteSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            chatMethod.unmuteSubCommand(false);
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat unmute");
            return true;
        }

        if (args.equalsIgnoreCase("-s")) {
            chatMethod.unmuteSubCommand(true);
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat unmute");
            return true;
        }

        playerMethod.sendMessage(sender, messages.getString("chat.flags.unknown-flag")
                .replace("%flag%", args));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }


    @Command(names = "cooldown")
    public boolean cooldownSubCommand(@Sender Player sender, @OptArg("") String text) {

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("chat", "cooldown", "time")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.equalsIgnoreCase("-d")) {
            chatMethod.setCooldown(sender, utils.getInt("chat.cooldown.text.seconds"));
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

        chatMethod.setCooldown(sender, time);
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
        return true;

    }

    @Command(names = "color")
    public boolean colorSubCommand(@Sender Player player, @OptArg("") String tag, @OptArg("") String color) {

        UserData userData = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (tag.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.empty-tags")
                    .replace("%tags%", chatMethod.allTags())
                    .replace("%usage%", moduleCheck.getUsage("chat", "color", "typetag", "[@tag/color]")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!chatMethod.isTag(tag)) {
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.unknown-tag")
                    .replace("%text%", tag));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        HashMap<String, String> tagData = userData.gethashTags();

        if (color.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.empty-colortags")
                    .replace("%colortags%", chatMethod.allColorTags())
                    .replace("%usage%", moduleCheck.getUsage("chat", "color", "typetag", "[@tag/color]")));
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

            playerMethod.sendMessage(player, command.getString("commands.chat.color.selected.tags")
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
                .replace("%color%", color.replace("&", "&#")));
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
