package code.commands;

import code.PluginService;
import code.bukkitutils.pages.PageCreator;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import code.utils.StringFormat;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.*;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(names = {"motd"}, desc = "Principal command")

public class MotdCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration utils;
    private final Configuration command;
    private final Configuration messages;

    private final PlayerMessage sender;
    private final ModuleCheck moduleCheck;
    private final List<String> motd;


    public MotdCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.utils = pluginService.getFiles().getBasicUtils();
        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();

        this.sender = pluginService.getPlayerMethods().getSender();
        this.moduleCheck = pluginService.getPathManager();
        this.motd = utils.getStringList("utils.join.motd.format");
    }

    @Command(names = "")
    public boolean mainCommand(@Sender Player player, @OptArg("1") int page) {

        StringFormat variable = pluginService.getStringFormat();

        if (page <= 0){
            sender.sendMessage(player, messages.getString("error.motd.negative-number")
                    .replace("%number%", String.valueOf(page)));

            return true;
        }

        PageCreator pageCreator = new PageCreator(motd);

        if (!pageCreator.pageExists(page - 1)){
            sender.sendMessage(player, messages.getString("error.motd.unknown-page")
                    .replace("%page%", String.valueOf(page)));
            return true;
        }

        List<String> motdPage = pageCreator.getHashString().get(page - 1);
        List<String> motdList = command.getStringList("commands.motd.list.message");

        motdList.replaceAll(text -> text
                                .replace("%page%", String.valueOf(page))
                                .replace("%maxpage%", String.valueOf(pageCreator.getMaxPage())));
        motdPage.replaceAll(text -> variable.replacePlayerVariables(player, text));

        motdList.forEach(text -> sender.sendMessage(player, text));
        sender.sendMessage(player, command.getString("commands.motd.list.space"));
        motdPage.forEach(text -> sender.sendMessage(player, text));
        sender.sendMessage(player, command.getString("commands.motd.list.space"));

        return true;
    }


    @Command(names = "addline")
    public boolean addLine(@Sender Player player, @OptArg("") @Text String text) {

        if (text.isEmpty()){
            sender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%",moduleCheck.getUsage( "motd", "addline/removeline/setline")));
            return true;
        }

        String message = String.join(" ", text);

        sender.sendMessage(player, command.getString("commands.motd.add-line")
                    .replace("%line%", message));

        motd.add(message);
        utils.set("utils.join.motd.format", motd);
        utils.save();
        return true;


    }

    @Command(names = "removeline")
    public boolean removeLine(@Sender Player player, @OptArg("1") int page) {

        String pageArg = String.valueOf(page);

        if (pageArg.isEmpty()) {
            sender.sendMessage(player, command.getString("commands.motd.remove-line")
                    .replace("%line%", motd.get(motd.size() - 1))
                    .replace("%number%", String.valueOf(motd.size())));
            motd.remove(motd.size() - 1);
        } else {

            String linePath;

            try {
                linePath = motd.get(page - 1);

            } catch (IndexOutOfBoundsException index) {
                sender.sendMessage(player, messages.getString("error.motd.unknown-line")
                        .replace("%line%", String.valueOf(page)));
                return true;

            }

            sender.sendMessage(player, command.getString("commands.motd.remove-line")
                    .replace("%line%", linePath)
                    .replace("%number%", String.valueOf(page)));
            motd.remove(page - 1);
        }


        utils.set("utils.join.motd.format", motd);
        utils.save();
        return true;
    }

    @Command(names = {"setline"})
    public boolean setLine(@Sender Player player, int page, @OptArg("") @Text String text) {

        if (text.isEmpty()){
            sender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "motd", "addline/removeline/setline")));
            return true;
        }

        String messagePath = String.join("", text);

        String linePath;

        try {
            linePath = motd.get(page - 1);

        } catch (IndexOutOfBoundsException index) {
            sender.sendMessage(player, messages.getString("error.motd.unknown-line")
                    .replace("%line%", String.valueOf(page)));
            return true;

        }
        motd.set(page - 1, messagePath);

        sender.sendMessage(player, command.getString("commands.motd.enableOption-line")
                .replace("%beforeline%", linePath)
                .replace("%line%", motd.get(page - 1))
                .replace("%number%", String.valueOf(page)));

        utils.set("utils.join.motd.format", motd);
        utils.save();
        return true;
    }

}
