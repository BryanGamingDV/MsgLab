package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import me.bryangaming.chatlab.debug.ErrorManager;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(names = {"announcer", "acc"})
public class AnnouncerCommand implements CommandClass {

    private PluginService pluginService;

    private Configuration command;
    private Configuration message;

    private PlayerMessage playerMethod;
    private ModuleCheck moduleCheck;

    public AnnouncerCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.command = pluginService.getFiles().getCommand();
        this.message = pluginService.getFiles().getMessages();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.moduleCheck = pluginService.getPathManager();
    }

    @Command(names = "")
    public boolean onMainSubCommand(@Sender Player sender) {
        playerMethod.sendMessage(sender, command.getStringList("commands.announcer.help"));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer");
        return true;
    }

    @Command(names = "add")
    public boolean onAddSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "add", "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", text);

        int announcerSize = command.getConfigurationSection("commands.announcer.config.messages").getKeys(false).size();

        List<String> announcerLines = new ArrayList<>();
        announcerLines.add(message);

        announcerSize++;
        command.set("commands.announcer.config.messages." + announcerSize, announcerLines);
        command.save();
        playerMethod.sendMessage(sender, command.getString("commands.announcer.add.announcer")
                .replace("%message%", String.join(" , ", message)));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer add");
        return true;
    }

    @Command(names = "addline")
    public boolean onAddLineSubCommand(@Sender Player sender, @OptArg("") String id, @OptArg("") @Text String text) {

        if (id.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "addline", "<id>", "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "addline", id, "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> announcerLines = command.getStringList("commands.announcer.config.messages." + id);

        if (announcerLines.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.announcer.unknown-id")
                    .replace("%id%", id));
            return true;
        }

        announcerLines.add(text);
        command.set("commands.announcer.config.messages." + id, announcerLines);
        command.save();
        playerMethod.sendMessage(sender, command.getString("commands.announcer.add.line")
                .replace("%message%", text));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer addline");
        return true;
    }

    @Command(names = "removeline")
    public boolean onRemoveLineSubCommand(@Sender Player sender, @OptArg("") String id, @OptArg("") String line) {

        if (id.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "removeline")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> announcerLines = command.getStringList("commands.announcer.config.messages." + id);

        if (announcerLines.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.announcer.unknown-id")
                    .replace("%id%", id));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (line.isEmpty()){
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                        .replace("%usage%", moduleCheck.getUsage("announcer", "removeline", id, "<line>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!ErrorManager.isNumber(line)){
            playerMethod.sendMessage(sender, message.getString("error.announcer.unknown-line")
                    .replace("%line%", line));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        announcerLines.remove(Integer.parseInt(line));
        command.set("commands.announcer.config.messages." + id, announcerLines);
        command.save();
        playerMethod.sendMessage(sender, command.getString("commands.announcer.remove.line")
                .replace("%id%", line));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer removeline");

        return true;

    }

    @Command(names = "setline")
    public boolean onSetLineSubCommand(@Sender Player sender, @OptArg("") String id, @OptArg("") String line, @OptArg("") @Text String text) {

        if (id.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "removeline")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> announcerLines = command.getStringList("commands.announcer.config.messages." + id);

        if (announcerLines.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.announcer.unknown-id")
                    .replace("%id%", id));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (line.isEmpty()){
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "setline", id, "<line>", "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!ErrorManager.isNumber(line)){
            playerMethod.sendMessage(sender, message.getString("error.announcer.unknown-line")
                    .replace("%line%", line));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()){
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "setline", id, line, "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }
        String message = String.join(" ", text);
        announcerLines.set(Integer.parseInt(line), message);
        command.set("commands.announcer.config.messages." + id, announcerLines);
        command.save();
        playerMethod.sendMessage(sender, command.getString("commands.announcer.remove.line")
                .replace("%id%", line));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer removeline");

        return true;

    }

    @Command(names = "remove")
    public boolean onRemoveSubCommand(@Sender Player sender, @OptArg("") String id) {
        if (id.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "add", "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (command.getStringList("commands.announcer.config.messages." + id).isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.announcer.unknown-id")
                    .replace("%id%", id));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        command.set("commands.announcer.config.messages." + id, "");
        command.save();
        playerMethod.sendMessage(sender, command.getString("commands.announcer.remove.announcer")
                    .replace("%id%", id));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer remove");
        return true;
    }

    @Command(names = "list")
    public boolean onListSubCommand(@Sender Player sender) {

        for (String text : command.getStringList("commands.announcer.list.format")) {
            if (text.contains("%loop-value%")) {
                int id = 1;

                for (String key : command.getConfigurationSection("commands.announcer.config.messages").getKeys(false)) {

                    for (String format : command.getStringList("commands.announcer.list.loop-value")) {

                        List<String> announcerList = command.getStringList("commands.announcer.config.messages." + key);

                        if (format.contains("%message%")) {

                            if (announcerList.size() > 1) {
                                for (String announcerPath : announcerList) {
                                    playerMethod.sendMessage(sender, format
                                            .replace("%message%", announcerPath));
                                }
                                continue;
                            }

                            playerMethod.sendMessage(sender, format
                                    .replace("%message%", announcerList.get(0)));
                            continue;
                        }

                        playerMethod.sendMessage(sender, format
                                .replace("%pst%", String.valueOf(id))
                                .replace("%id%", key));
                        id++;
                    }
                }
                continue;
            }

            playerMethod.sendMessage(sender, text);
        }
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer list");
        return true;
    }

    @Command(names = "set")
    public boolean onSetSubCommand(@Sender Player sender, @OptArg("") String arg1, @OptArg("") String arg2) {
        if (arg1.isEmpty()) {
            playerMethod.sendMessage(sender, message.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("announcer", "set", "interval/mode")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.equalsIgnoreCase("interval")) {
            if (arg2.isEmpty()) {
                playerMethod.sendMessage(sender, message.getString("error.no-arg")
                        .replace("%usage%", moduleCheck.getUsage("announcer", "set", arg1, "[<time>]")));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }
            int number;
            try {
                number = Integer.parseInt(arg2);
            } catch (NumberFormatException numberFormatException) {
                playerMethod.sendMessage(sender, message.getString("error.announcer.interval")
                        .replace("%time%", arg2));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.announcer.set.interval")
                    .replace("%time%", String.valueOf(number)));
            command.set("commands.announcer.config.interval", number);
            command.save();
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "announcer interval");
            return true;
        }

        if (arg1.equalsIgnoreCase("mode")) {
            if (arg2.isEmpty()) {
                playerMethod.sendMessage(sender, message.getString("error.no-arg")
                        .replace("%usage%", moduleCheck.getUsage("announcer", "set", arg1, "ordened/random")));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            switch (arg2) {
                case "ordened":
                case "random":
                    playerMethod.sendMessage(sender, command.getString("commands.announcer.set.interval")
                            .replace("%mode%", arg2));
                    command.set("commands.announcer.config.mode", arg2);
                    command.save();
                    break;
                default:
                    playerMethod.sendMessage(sender, message.getString("error.announcer.mode")
                            .replace("%mode%", arg2));
            }
            return true;
        }
        return true;
    }
}
