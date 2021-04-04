package atogesputo.bryangaming.chatlab.commands;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import atogesputo.bryangaming.chatlab.utils.StringFormat;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import atogesputo.bryangaming.chatlab.bukkitutils.pages.PageCreator;
import atogesputo.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import atogesputo.bryangaming.chatlab.utils.module.ModuleCheck;
import org.bukkit.entity.Player;

import java.util.List;

@Command(names = {"motd"}, desc = "Principal command")

public class MotdCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration utils;
    private final Configuration command;
    private final Configuration messages;

    private final PlayerMessage playerMethod;
    private final ModuleCheck moduleCheck;
    private final List<String> motd;


    public MotdCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.utils = pluginService.getFiles().getBasicUtils();
        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.moduleCheck = pluginService.getPathManager();
        this.motd = utils.getStringList("lobby.formats.default.join.motd");
    }

    @Command(names = "")
    public boolean mainCommand(@Sender Player sender, @OptArg("1") int page) {


        StringFormat variable = pluginService.getStringFormat();

        if (page <= 0) {
            playerMethod.sendMessage(sender, messages.getString("error.motd.negative-number")
                    .replace("%number%", String.valueOf(page)));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        PageCreator pageCreator = new PageCreator(motd);

        if (!pageCreator.pageExists(page - 1)) {
            playerMethod.sendMessage(sender, messages.getString("error.motd.unknown-page")
                    .replace("%page%", String.valueOf(page)));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> motdPage = pageCreator.getHashString().get(page - 1);
        List<String> motdList = command.getStringList("commands.motd.list.message");

        motdList.replaceAll(text -> text
                .replace("%page%", String.valueOf(page))
                .replace("%maxpage%", String.valueOf(pageCreator.getMaxPage())));
        motdPage.replaceAll(text -> variable.replacePlayerVariables(sender, text));

        motdList.forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendMessage(sender, command.getString("commands.motd.list.space"));
        motdPage.forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendMessage(sender, command.getString("commands.motd.list.space"));

        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "motd");
        return true;
    }


    @Command(names = "addline")
    public boolean addLine(@Sender Player sender, @OptArg("") @Text String text) {

        if (!playerMethod.hasPermission(sender, "commands.motd.admin")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("motd", "addline/removeline/setline")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", text);

        playerMethod.sendMessage(sender, command.getString("commands.motd.add-line")
                .replace("%line%", message));

        motd.add(message);
        utils.set("lobby.formats.default.join.motd", motd);
        utils.save();
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "motd addline");
        return true;


    }

    @Command(names = "removeline")
    public boolean removeLine(@Sender Player sender, @OptArg("1") int page) {

        if (!playerMethod.hasPermission(sender, "commands.motd.admin")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String pageArg = String.valueOf(page);

        if (pageArg.isEmpty()) {
            playerMethod.sendMessage(sender, command.getString("commands.motd.remove-line")
                    .replace("%line%", motd.get(motd.size() - 1))
                    .replace("%number%", String.valueOf(motd.size())));
            motd.remove(motd.size() - 1);
        } else {
            String linePath;

            try {
                linePath = motd.get(page - 1);

            } catch (IndexOutOfBoundsException index) {
                playerMethod.sendMessage(sender, messages.getString("error.motd.unknown-line")
                        .replace("%line%", String.valueOf(page)));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;

            }

            playerMethod.sendMessage(sender, command.getString("commands.motd.remove-line")
                    .replace("%line%", linePath)
                    .replace("%number%", String.valueOf(page)));
            motd.remove(page - 1);
        }


        utils.set("lobby.formats.default.join.motd", motd);
        utils.save();
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "motd removeline");
        return true;
    }

    @Command(names = {"setline"})
    public boolean setLine(@Sender Player sender, @OptArg("-1") int page, @OptArg("") @Text String text) {

        if (!playerMethod.hasPermission(sender, "commands.motd.admin")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }
        if (page < 0) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("motd", "setline", "<page>", "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (text.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("motd", "setline", "<page>", "<text>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String messagePath = String.join("", text);

        String linePath;

        try {
            linePath = motd.get(page - 1);

        } catch (IndexOutOfBoundsException index) {
            playerMethod.sendMessage(sender, messages.getString("error.motd.unknown-line")
                    .replace("%line%", String.valueOf(page)));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;

        }
        motd.set(page - 1, messagePath);

        playerMethod.sendMessage(sender, command.getString("commands.motd.set-line")
                .replace("%beforeline%", linePath)
                .replace("%line%", motd.get(page - 1))
                .replace("%number%", String.valueOf(page)));

        utils.set("lobby.formats.default.join.motd", motd);
        utils.save();
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "motd setline");
        return true;
    }

}
