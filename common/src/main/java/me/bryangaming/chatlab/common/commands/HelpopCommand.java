package me.bryangaming.chatlab.common.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.commands.HelpOpManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.HelpOpEvent;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.wrapper.ServerWrapper;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.bryangaming.chatlab.common.wrapper.annotation.SenderAnnotWrapper;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.ArrayList;
import java.util.List;

@Command(names = {"helpop", "ac"})
public class HelpopCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;
    private final HelpOpManager helpOpManager;

    private final Configuration commandFile;
    private final Configuration messagesFile;

    public HelpopCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.helpOpManager = pluginService.getPlayerManager().getHelpOpMethod();

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onSubCommand(@SenderAnnotWrapper  PlayerWrapper sender, @OptArg("") @Text String args) {

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("helpop", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.helpop.received")
                .replace("%player%", sender.getName()));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop");

        String message = String.join(" ", args);

        if (commandFile.getBoolean("commands.helpop.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT,"Receive");
            pluginService.getEventLoader().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
                return true;
            }
        }

        pluginService.getEventLoader().callEvent(new HelpOpEvent(message));
        return true;
    }

    @Command(names = "-list")
    public boolean onListSubCommand(@SenderAnnotWrapper  PlayerWrapper sender) {

        List<String> helpopList = new ArrayList<>();

        for (PlayerWrapper playeronline : ServerWrapper.getData().getOnlinePlayers()) {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(playeronline.getUniqueId());

            if (senderManager.hasPermission(sender, "commands.helpop.watch") && onlineCache.isPlayerHelpOp()) {
                helpopList.add(playeronline.getName());
            }
        }

        if (helpopList.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.helpop.anybody"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.helpop.list-helpop"));

        for (String helpopPlayers : helpopList) {
            senderManager.sendMessage(sender, "&8- &f" + helpopPlayers);
        }

        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -list");
        return true;
    }

    @Command(names = "-on")
    public boolean onOnSubCommand(@SenderAnnotWrapper  PlayerWrapper sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(senderManager.hasPermission(sender, "commands.helpop.watch"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (userData.isPlayerHelpOp()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.helpop.activated"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        helpOpManager.enableOption(sender.getUniqueId());
        senderManager.sendMessage(sender, commandFile.getString("commands.helpop.player.enabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -on");
        return true;
    }

    @Command(names = "-off")
    public boolean onOffSubCommand(@SenderAnnotWrapper  PlayerWrapper sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(senderManager.hasPermission(sender, "commands.helpop.watch"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(userData.isPlayerHelpOp())) {
            senderManager.sendMessage(sender, messagesFile.getString("error.helpop.unactivated"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        helpOpManager.disableOption(sender.getUniqueId());
        senderManager.sendMessage(sender, commandFile.getString("commands.helpop.player.disabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -off");
        return true;
    }

    @Command(names = "-toggle")
    public boolean onToggleSubCommand(@SenderAnnotWrapper  PlayerWrapper sender) {

        if (!(senderManager.hasPermission(sender, "commands.helpop.watch"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            return true;
        }

        helpOpManager.toggleOption(sender.getUniqueId());
        senderManager.sendMessage(sender, commandFile.getString("commands.helpop.player.toggle")
                .replace("%mode%", helpOpManager.getStatus()));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -toggle");
        return true;
    }
}
