package org.bryangaming.chatlab.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import org.bryangaming.chatlab.data.UserData;
import org.bryangaming.chatlab.events.HelpOpEvent;
import org.bryangaming.chatlab.managers.commands.HelpOpMethod;
import org.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bryangaming.chatlab.revisor.RevisorManager;
import org.bryangaming.chatlab.utils.Configuration;
import org.bryangaming.chatlab.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(names = {"helpop", "ac"})
public class HelpopCommand implements CommandClass {

    private final PluginService pluginService;

    private final ModuleCheck moduleCheck;

    private final PlayerMessage playerMethod;
    private final HelpOpMethod helpOpMethod;

    private final Configuration command;
    private final Configuration messages;

    public HelpopCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.moduleCheck = pluginService.getPathManager();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.helpOpMethod = pluginService.getPlayerMethods().getHelpOpMethod();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onSubCommand(@Sender Player sender, @OptArg("") @Text String args) {

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("helpop", "<message>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.helpop.received")
                .replace("%player%", sender.getName()));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "helpop");

        String message = String.join(" ", args);

        if (command.getBoolean("commands.helpop.enable-revisor")) {
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(sender.getUniqueId(), message);

            if (message == null) {
                return true;
            }
        }

        Bukkit.getPluginManager().callEvent(new HelpOpEvent(message));
        return true;
    }

    @Command(names = "-list")
    public boolean onListSubCommand(@Sender Player sender) {

        List<String> helpopList = new ArrayList<>();

        for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(playeronline.getUniqueId());

            if (playerMethod.hasPermission(sender, "commands.helpop.watch") && onlineCache.isPlayerHelpOp()) {
                helpopList.add(playeronline.getName());
            }
        }

        if (helpopList.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.helpop.anybody"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.helpop.list-helpop"));

        for (String helpopPlayers : helpopList) {
            playerMethod.sendMessage(sender, "&8- &f" + helpopPlayers);
        }

        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "helpop -list");
        return true;
    }

    @Command(names = "-on")
    public boolean onOnSubCommand(@Sender Player sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(playerMethod.hasPermission(sender, "commands.helpop.watch"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (userData.isPlayerHelpOp()) {
            playerMethod.sendMessage(sender, messages.getString("error.helpop.activated"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        helpOpMethod.enableOption(sender.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.helpop.player.enabled"));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "helpop -on");
        return true;
    }

    @Command(names = "-off")
    public boolean onOffSubCommand(@Sender Player sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(playerMethod.hasPermission(sender, "commands.helpop.watch"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(userData.isPlayerHelpOp())) {
            playerMethod.sendMessage(sender, messages.getString("error.helpop.unactivated"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        helpOpMethod.disableOption(sender.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.helpop.player.disabled"));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "helpop -off");
        return true;
    }

    @Command(names = "-toggle")
    public boolean onToggleSubCommand(@Sender Player sender) {

        if (!(playerMethod.hasPermission(sender, "commands.helpop.watch"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;
        }

        helpOpMethod.toggleOption(sender.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.helpop.player.toggle")
                .replace("%mode%", helpOpMethod.getStatus()));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "helpop -toggle");
        return true;
    }
}
