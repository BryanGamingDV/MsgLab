package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.pages.PageCreator;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(names = {"motd"}, desc = "Principal command")

public class MotdCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration formatFile;
    private final Configuration commandFile;
    private final Configuration messagesFile;

    private final SenderManager senderManager;
    private final List<String> motd;


    public MotdCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.formatFile = pluginService.getFiles().getFormatsFile();
        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.motd = formatFile.getStringList("lobby.formats.default.join.motd");
    }

    @Command(names = "")
    public boolean mainCommand(@Sender Player sender, @OptArg("1") int page) {

        if (page <= 0) {
            senderManager.sendMessage(sender, messagesFile.getString("error.motd.negative-number")
                    .replace("%number%", String.valueOf(page)));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

         
        PageCreator pageCreator = new PageCreator(motd);

        if (!pageCreator.pageExists(page - 1)) {
            senderManager.sendMessage(sender, messagesFile.getString("error.motd.unknown-page")
                    .replace("%page%", String.valueOf(page)));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> motdPage = pageCreator.getHashString().get(page - 1);
        List<String> motdList = commandFile.getStringList("commands.motd.list.message");

        motdList.replaceAll(text -> text
                .replace("%page%", String.valueOf(page))
                .replace("%maxpage%", String.valueOf(pageCreator.getMaxPage())));

        motdList.forEach(text -> senderManager.sendMessage(sender, text));
        senderManager.sendMessage(sender, commandFile.getString("commands.motd.list.space"));
        motdPage.forEach(text -> senderManager.sendMessage(sender, text));
        senderManager.sendMessage(sender, commandFile.getString("commands.motd.list.space"));

        senderManager.playSound(sender, SoundEnum.ARGUMENT, "motd");
        return true;
    }


    @Command(names = "addline")
    public boolean addLine(@Sender Player sender, @OptArg("") @Text String text) {

        if (!senderManager.hasPermission(sender, "commands.motd.admin")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("motd", "addline/removeline/setline")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", text);

        senderManager.sendMessage(sender, commandFile.getString("commands.motd.add-line")
                .replace("%line%", message));

        motd.add(message);
        formatFile.set("lobby.formats.default.join.motd", motd);
        formatFile.save();
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "motd addline");
        return true;


    }

    @Command(names = "removeline")
    public boolean removeLine(@Sender Player sender, @OptArg("1") int page) {

        if (!senderManager.hasPermission(sender, "commands.motd.admin")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String pageArg = String.valueOf(page);

        if (pageArg.isEmpty()) {
            senderManager.sendMessage(sender, commandFile.getString("commands.motd.remove-line")
                    .replace("%line%", motd.get(motd.size() - 1))
                    .replace("%number%", String.valueOf(motd.size())));
            motd.remove(motd.size() - 1);
        } else {
            String linePath;

            try {
                linePath = motd.get(page - 1);

            } catch (IndexOutOfBoundsException index) {
                senderManager.sendMessage(sender, messagesFile.getString("error.motd.unknown-line")
                        .replace("%line%", String.valueOf(page)));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;

            }

            senderManager.sendMessage(sender, commandFile.getString("commands.motd.remove-line")
                    .replace("%line%", linePath)
                    .replace("%number%", String.valueOf(page)));
            motd.remove(page - 1);
        }


        formatFile.set("lobby.formats.default.join.motd", motd);
        formatFile.save();
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "motd removeline");
        return true;
    }

    @Command(names = {"setline"})
    public boolean setLine(@Sender Player sender, @OptArg("-1") int page, @OptArg("") @Text String text) {

        if (!senderManager.hasPermission(sender, "commands.motd.admin")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }
        if (page < 0) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("motd", "setline", "<page>", "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("motd", "setline", "<page>", "<text>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String messagePath = String.join("", text);

        String linePath;

        try {
            linePath = motd.get(page - 1);

        } catch (IndexOutOfBoundsException index) {
            senderManager.sendMessage(sender, messagesFile.getString("error.motd.unknown-line")
                    .replace("%line%", String.valueOf(page)));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;

        }
        motd.set(page - 1, messagePath);

        senderManager.sendMessage(sender, commandFile.getString("commands.motd.set-line")
                .replace("%beforeline%", linePath)
                .replace("%line%", motd.get(page - 1))
                .replace("%number%", String.valueOf(page)));

        formatFile.set("lobby.formats.default.join.motd", motd);
        formatFile.save();
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "motd setline");
        return true;
    }

}
