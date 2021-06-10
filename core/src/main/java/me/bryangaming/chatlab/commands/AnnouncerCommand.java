package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(names = {"announcer", "acc"})
public class AnnouncerCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration configFile;
    private final Configuration messagesFile;

    private final SenderManager senderManager;

    public AnnouncerCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean onMainSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getStringList("announcer.help"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer");
        return true;
    }

    @Command(names = "add")
    public boolean onAddSubCommand(@Sender Player sender, @OptArg("") @Text String text) {

        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "add", "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", text);

        int announcerSize = configFile.getConfigurationSection("modules.announcer.announcers.messages").getKeys(false).size();

        List<String> announcerLines = new ArrayList<>();
        announcerLines.add(message);

        announcerSize++;
        configFile.set("modules.announcer.announcers.messages." + announcerSize, announcerLines);
        configFile.save();
        senderManager.sendMessage(sender, messagesFile.getString("announcer.add.announcer")
                .replace("%message%", String.join(" , ", message)));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer add");
        return true;
    }

    @Command(names = "addline")
    public boolean onAddLineSubCommand(@Sender Player sender, @OptArg("") String id, @OptArg("") @Text String text) {

        if (id.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "addline", "<id>", "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "addline", id, "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> announcerLines = configFile.getStringList("modules.announcer.config.messages." + id);

        if (announcerLines.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-id")
                    .replace("%id%", id));
            return true;
        }

        announcerLines.add(text);
        configFile.set("modules.announcer.announcers.messages." + id, announcerLines);
        configFile.save();
        senderManager.sendMessage(sender, messagesFile.getString("announcer.add.line")
                .replace("%message%", text));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer addline");
        return true;
    }

    @Command(names = "removeline")
    public boolean onRemoveLineSubCommand(@Sender Player sender, @OptArg("") String id, @OptArg("") String line) {

        if (id.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "removeline")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> announcerLines = configFile.getStringList("modules.announcer.announcers.messages." + id);

        if (announcerLines.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-id")
                    .replace("%id%", id));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (line.isEmpty()){
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                        .replace("%usage%", TextUtils.getUsage("announcer", "removeline", id, "<line>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!TextUtils.isNumber(line)){
            senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-line")
                    .replace("%line%", line));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        announcerLines.remove(Integer.parseInt(line));
        configFile.set("modules.announcer.announcers.messages." + id, announcerLines);
        configFile.save();
        senderManager.sendMessage(sender, messagesFile.getString("announcer.remove.line")
                .replace("%id%", line));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer removeline");

        return true;

    }

    @Command(names = "setline")
    public boolean onSetLineSubCommand(@Sender Player sender, @OptArg("") String id, @OptArg("") String line, @OptArg("") @Text String text) {

        if (id.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "setline")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> announcerLines = configFile.getStringList("modules.announcer.announcers.messages." + id);

        if (announcerLines.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-id")
                    .replace("%id%", id));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (line.isEmpty()){
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "setline", id, "<line>", "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!TextUtils.isNumber(line)){
            senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-line")
                    .replace("%line%", line));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()){
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "setline", id, line, "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }
        String message = String.join(" ", text);
        announcerLines.set(Integer.parseInt(line), message);
        configFile.set("modules.announcer.announcers.messages." + id, announcerLines);
        configFile.save();
        senderManager.sendMessage(sender, messagesFile.getString("announcer.set-line")
                .replace("%id%", line));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer setline");

        return true;

    }

    @Command(names = "remove")
    public boolean onRemoveSubCommand(@Sender Player sender, @OptArg("") String id) {
        if (id.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "add", "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (configFile.getStringList("modules.announcer.announcers.messages." + id).isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-id")
                    .replace("%id%", id));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        configFile.set("modules.announcer.announcers.messages." + id, "");
        configFile.save();
        senderManager.sendMessage(sender, messagesFile.getString("announcer.remove.announcer")
                    .replace("%id%", id));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer remove");
        return true;
    }

    @Command(names = "list")
    public boolean onListSubCommand(@Sender Player sender) {

        for (String text : messagesFile.getStringList("announcer.list.format")) {
            if (text.contains("%loop-value%")) {
                int id = 1;

                for (String key : configFile.getConfigurationSection("modules.announcer.announcers.messages").getKeys(false)) {

                    for (String format : messagesFile.getStringList("announcer.list.loop-value")) {

                        List<String> announcerList = configFile.getStringList("modules.announcer.announcers.messages." + key);

                        if (format.contains("%message%")) {

                            if (announcerList.size() > 1) {
                                for (String announcerPath : announcerList) {
                                    senderManager.sendMessage(sender, format
                                            .replace("%message%", announcerPath));
                                }
                                continue;
                            }

                            senderManager.sendMessage(sender, format
                                    .replace("%message%", announcerList.get(0)));
                            continue;
                        }

                        senderManager.sendMessage(sender, format
                                .replace("%pst%", String.valueOf(id))
                                .replace("%id%", key));
                        id++;
                    }
                }
                continue;
            }

            senderManager.sendMessage(sender, text);
        }
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer list");
        return true;
    }

    @Command(names = "set")
    public boolean onSetSubCommand(@Sender Player sender, @OptArg("") String arg1, @OptArg("") String arg2) {

        if (arg1.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("announcer", "set", "interval/mode")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.equalsIgnoreCase("interval")) {
            if (arg2.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                        .replace("%usage%", TextUtils.getUsage("announcer", "set", arg1, "[<time>]")));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            if (TextUtils.isNumber(arg2)) {
                senderManager.sendMessage(sender, messagesFile.getString("announcer.error.interval")
                        .replace("%time%", arg2));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            int number = Integer.parseInt(arg2);

            senderManager.sendMessage(sender, messagesFile.getString("announcer.set.interval")
                    .replace("%time%", String.valueOf(number)));
            configFile.set("modules.announcer.announcers.interval", number);
            configFile.save();
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "announcer interval");
            return true;
        }

        if (arg1.equalsIgnoreCase("mode")) {
            if (arg2.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                        .replace("%usage%", TextUtils.getUsage("announcer", "set", arg1, "ordened/random")));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            switch (arg2) {
                case "ordened":
                case "random":
                    senderManager.sendMessage(sender, messagesFile.getString("announcer.set.interval")
                                .replace("%mode%", arg2));
                    configFile.set("modules.announcer.announcers.type", arg2);
                    configFile.save();
                    break;
                default:
                    senderManager.sendMessage(sender, messagesFile.getString("announcer.error.unknown-mode")
                            .replace("%mode%", arg2));
            }
            return true;
        }
        return true;
    }
}
