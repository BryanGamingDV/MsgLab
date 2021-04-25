package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.bukkitutils.sound.SoundEnum;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.HelpOpEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.commands.HelpOpManager;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(names = {"helpop", "ac"})
public class HelpopCommand implements CommandClass {

    private final PluginService pluginService;

    private final ModuleCheck moduleCheck;

    private final PlayerMessage playerMethod;
    private final HelpOpManager helpOpManager;

    private final Configuration command;
    private final Configuration messages;

    public HelpopCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.moduleCheck = pluginService.getPathManager();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.helpOpManager = pluginService.getPlayerMethods().getHelpOpMethod();

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
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT,"Receive");
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
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

        helpOpManager.enableOption(sender.getUniqueId());
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

        helpOpManager.disableOption(sender.getUniqueId());
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

        helpOpManager.toggleOption(sender.getUniqueId());
        playerMethod.sendMessage(sender, command.getString("commands.helpop.player.toggle")
                .replace("%mode%", helpOpManager.getStatus()));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "helpop -toggle");
        return true;
    }
}
