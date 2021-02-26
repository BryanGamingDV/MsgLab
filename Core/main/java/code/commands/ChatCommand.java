package code.commands;

import code.MsgLab;
import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.data.UserData;
import code.methods.commands.ChatMethod;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
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
import java.util.UUID;


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

    private final SoundCreator sound;


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

        ModuleCheck moduleCheck = pluginService.getPathManager();
        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();

        UUID playeruuid = sender.getUniqueId();

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("chat", "help, reload, clear, mute, unmute, cooldown, color")));
        sound.setSound(playeruuid, "sounds.error");
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        StringFormat variable = pluginService.getStringFormat();
        variable.loopString(sender, command, "commands.chat.help");
        return true;

    }

    @Command(names = "clear")
    public boolean clearSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        int lines = command.getInt("command.chat.clear.default-blank");

        for (int id = 0; id < strings.length; id++) {
            if (strings[id].equalsIgnoreCase("-w")){
                if (strings[id + 1].isEmpty()){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-world")
                            .replace("%world%", text));
                    return true;
                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-l")){

                try {
                    lines = Integer.parseInt(strings[id + 1]);
                }catch (NumberFormatException numberFormatException){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-number")
                            .replace("%world%", strings[id + 1]));
                    return true;
                }
                id++;
                continue;
            }

            break;
        }
        chatMethod.clearSubCommand(sender, lines, world);

        return true;
    }
    @Command(names = "mute")
    public boolean muteSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        String[] strings = text.split(" ");

        String world = "-global";
        int times = -1;

        for (int id = 0; id < strings.length; id++) {
            if (strings[id].equalsIgnoreCase("-w")){
                if (strings[id + 1].isEmpty()){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    return true;
                }

                if (Bukkit.getWorld(strings[id + 1]) == null && !strings[id + 1].equalsIgnoreCase("-global")){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-world")
                                .replace("%world%", text));
                    return true;

                }

                world = strings[id + 1];
                id++;
                continue;
            }

            if (strings[id].equalsIgnoreCase("-t")){

                if (strings[id + 1].isEmpty()){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.empty-flag"));
                    return true;
                }

                try {
                    times = Integer.parseInt(strings[id + 1]);
                }catch (NumberFormatException numberFormatException){
                    playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-number")
                            .replace("%world%", strings[id + 1]));
                    return true;
                }

                id++;
                continue;
            }

            break;
        }

        chatMethod.muteSubCommand(sender, times, world);
        return true;

    }

    @Command(names = "unmute")
    public boolean unmuteSubCommand(@Sender Player sender) {
        chatMethod.unmuteSubCommand();
        return true;
    }


    @Command(names = "cooldown")
    public boolean cooldownSubCommand(@Sender Player sender, @OptArg("") String text) {

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("chat", "cooldown", "time")));
            sound.setSound(sender.getUniqueId(), "sounds.error");
            return true;
        }

        if (text.equalsIgnoreCase("-d")){
            chatMethod.setCooldown(sender, utils.getInt("chat.cooldown.text.seconds"));
            return true;
        }

        int time;

        try {
            time = Integer.parseInt(text);
        }catch (NumberFormatException nfe){
            playerMethod.sendMessage(sender, messages.getString("error.flags.unknown-number")
                    .replace("%text%", text));
            return true;
        }

        chatMethod.setCooldown(sender, time);
        return true;

    }

    @Command(names = "color")
    public boolean colorSubCommand(@Sender Player player, @OptArg("") String tag, @OptArg("") String color){

        UserData userData = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (tag.isEmpty()){
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.empty-tag")
                    .replace("%tags%", chatMethod.allTags()));
            return true;
        }

        if (!chatMethod.isTag(tag)){
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.unknown-tag")
                    .replace("%text%", tag));
            return true;
        }

        HashMap<String, String> tagData = userData.getHashMap();

        if (color.isEmpty()){
            playerMethod.sendMessage(player, messages.getString("error.chat.tags.empty-colortag")
                    .replace("%colortags%", chatMethod.allColorTags()));
            return true;
        }

        if (color.startsWith("@")) {
            if (command.getString("commands.chat.color.tags." + color.substring(1)) == null) {
                playerMethod.sendMessage(player, messages.getString("error.chat.tags.unknown-tagcolor")
                        .replace("%text%", color));
                return true;
            }

            if (!tagData.containsKey(tag)){
                tagData.replace(tag, color.substring(1));
            }else{
                tagData.put(tag, color.substring(1));
            }

            playerMethod.sendMessage(player, command.getString("commands.chat.color.selected.tags"
                    .replace("%color%", color.substring(1))));
            return true;
        }

        if (!tagData.containsKey(tag)){
            tagData.replace(tag, color);
        }else{
            tagData.put(tag, color);
        }
        playerMethod.sendMessage(player, command.getString("commands.chat.color.selected.color"
                .replace("%color%", color)));
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender) {

        if (!playerMethod.hasPermission(sender, "commands.chat.reload")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;

        }

        playerMethod.sendMessage(sender, command.getString("commands.bmsg.load"));
        this.getReloadEvent(sender);
        return true;
    }

    public void getReloadEvent(Player sender) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

                ConfigManager files = pluginService.getFiles();
                files.getBasicUtils().reload();
                playersender.sendMessage(sender, files.getCommand().getString("commands.bmsg.reload"));
            }

        }, 20L * 3);
    }
}
