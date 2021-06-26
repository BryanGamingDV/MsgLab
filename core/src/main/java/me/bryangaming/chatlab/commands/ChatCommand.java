package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.ChatManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@Command(names = {"chat"})
public class ChatCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration configFile;
    private final Configuration messagesFile;

    private final Configuration filtersFile;
    private final Configuration formatFile;

    private final ServerData serverData;

    private final ChatManager chatManager;
    private final SenderManager senderManager;


    public ChatCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.formatFile = pluginService.getFiles().getFormatsFile();
        this.filtersFile = pluginService.getFiles().getFiltersFile();

        this.serverData = pluginService.getServerData();

        this.chatManager = pluginService.getPlayerManager().getChatManager();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean mainSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                .replace("%usage%", TextUtils.getUsage("chat", "help, reload, clear, mute, unmute, cooldown, color")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        messagesFile.getStringList("chat.help")
                .forEach(text -> senderManager.sendMessage(sender, text));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat help");
        return true;

    }

    @Command(names = "clear")
    public boolean clearSubCommand(@Sender Player sender, @OptArg("") @Text String arguments) {

        String world = "-global";
        int lines = configFile.getInt("modules.chat.empty-blank", 50);
        boolean silent = false;

        if (arguments.isEmpty()) {
            chatManager.clearSubCommand(sender, lines, world, false);
            return true;
        }

        String[] flags = arguments.split(" ");

        for (int id = 0; id < flags.length; id++) {

            if (flags[id].equalsIgnoreCase("-silent")) {
                silent = true;
                continue;
            }
            if (flags[id].equalsIgnoreCase("-w")) {

                if (flags[id + 1].isEmpty()) {
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.empty-flag"));
                    return true;
                }

                if (Bukkit.getWorld(flags[id + 1]) == null && !flags[id + 1].equalsIgnoreCase("-global")) {
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-world")
                            .replace("%world%", flags[id + 1]));
                    return true;
                }

                world = flags[id + 1];
                id++;
                continue;
            }

            if (flags[id].equalsIgnoreCase("-l")) {

                try {
                    lines = Integer.parseInt(flags[id + 1]);
                } catch (NumberFormatException numberFormatException) {
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-number")
                            .replace("%world%", flags[id + 1]));

                    return true;
                }
                id++;
                continue;
            }

            senderManager.playSound(sender, SoundEnum.ERROR);
            senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-flag")
                    .replace("%flag%", flags[id]));
            break;
        }

        chatManager.clearSubCommand(sender, lines, world, silent);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat clear");
        return true;
    }

    @Command(names = "mute")
    public boolean muteSubCommand(@Sender Player sender, @OptArg("") @Text String arguments) {

        String[] flags = arguments.split(" ");

        String world = "-global";
        String channel = "-none";

        int times = -1;
        boolean silent = false;

        if (arguments.isEmpty()) {
            chatManager.muteSubCommand(sender, times, channel, world, false);
            return true;
        }


        for (int id = 0; id < flags.length; id++) {
            switch (flags[id]) {
                case "-silent":
                    silent = true;
                    continue;
                case "-w":
                    if (flags[id + 1].isEmpty()) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.empty-flag"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    if (Bukkit.getWorld(flags[id + 1]) == null && !flags[id + 1].equalsIgnoreCase("-global")) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-world")
                                .replace("%world%", flags[id + 1]));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;

                    }

                    if (flags[id + 1].equalsIgnoreCase("-global")) {
                        if (serverData.isMuted()) {
                            senderManager.sendMessage(sender, messagesFile.getString("error.chat.management.already-muted"));
                            senderManager.playSound(sender, SoundEnum.ERROR);
                            return true;
                        }
                    }

                    world = flags[id + 1];
                    id++;
                    continue;
                case "-c":
                    if (flags[id + 1].isEmpty()) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.empty-flag"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    if (formatFile.getString("channel." + channel) == null && !flags[id + 1].equalsIgnoreCase("-none")) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-channel")
                                .replace("%channel%", arguments));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    channel = flags[id + 1];
                    id++;
                    continue;
                case "-t":
                    if (flags[id + 1].isEmpty()) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.empty-flag"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    try {
                        times = Integer.parseInt(flags[id + 1]);
                    } catch (NumberFormatException numberFormatException) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-number")
                                .replace("%seconds%", flags[id + 1]));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    id++;
                    continue;
                default:
                    senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-flag")
                            .replace("%flag%", flags[id]));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
            }
        }

        chatManager.muteSubCommand(sender, times, channel, world, silent);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat mute");
        return true;

    }

    @Command(names = "unmute")
    public boolean unmuteSubCommand(@Sender Player sender, @OptArg("") @Text String arguments) {

        String world = "-global";
        String channel = "-none";

        boolean silent = false;

        if (arguments.isEmpty()){
            chatManager.unmuteSubCommand(world, channel, silent);
            return true;
        }
        String[] flags = arguments.split(" ");

        for (int id = 0; id < flags.length; id++) {

            switch (flags[id]) {
                case "-silent":
                    silent = true;
                    continue;

                case "-w":
                    if (flags[id + 1].isEmpty()) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.empty-flag"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    if (Bukkit.getWorld(flags[id + 1]) == null && !flags[id + 1].equalsIgnoreCase("-global")) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-world")
                                .replace("%world%", flags[id + 1]));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;

                    }

                    if (flags[id + 1].equalsIgnoreCase("-global")) {
                        if (!serverData.isMuted()) {
                            senderManager.sendMessage(sender, messagesFile.getString("error.chat.management.already-unmuted"));
                            senderManager.playSound(sender, SoundEnum.ERROR);
                            return true;
                        }
                    }

                    world = flags[id + 1];
                    id++;
                    continue;
                case "-c":
                    if (flags[id + 1].isEmpty()) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.empty-flag"));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    if (formatFile.getConfigurationSection("channel." + channel) == null && !flags[id + 1].equalsIgnoreCase("-none")) {
                        senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-channel")
                                .replace("%channel%", flags[id + 1]));
                        senderManager.playSound(sender, SoundEnum.ERROR);
                        return true;
                    }

                    channel = flags[id + 1];
                    id++;
                    continue;
                default:
                    senderManager.sendMessage(sender, messagesFile.getString("chat.flags.unknown-flag")
                            .replace("%flag%", arguments));
                    senderManager.playSound(sender, SoundEnum.ERROR);
                    return true;
            }
        }

        chatManager.unmuteSubCommand(world, channel, silent);
        return true;
    }


    @Command(names = "cooldown")
    public boolean cooldownSubCommand(@Sender Player sender, @OptArg("") String argument1) {

        if (argument1.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("chat", "cooldown", "time")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (argument1.equalsIgnoreCase("-d")) {
            chatManager.setCooldown(sender, filtersFile.getInt("cooldown.text.seconds"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
            return true;
        }

        if (!TextUtils.isNumber(argument1)){
            senderManager.sendMessage(sender, messagesFile.getString("chat.error.flags.unknown-number")
                    .replace("%text%", argument1));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }
        int time = Integer.parseInt(argument1);

        chatManager.setCooldown(sender, time);
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat cooldown");
        return true;

    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender) {

        if (!senderManager.hasPermission(sender, "chat", "reload")) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;

        }

        formatFile.reload();
        senderManager.sendMessage(sender, messagesFile.getString("chat.reload"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "chat reload");
        return true;
    }

}
