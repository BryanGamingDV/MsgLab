package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.HelpOpEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.HelpOpManager;
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

import java.util.ArrayList;
import java.util.List;

@Command(names = {"helpop", "ac"})
public class HelpopCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;
    private final HelpOpManager helpOpManager;

    private final Configuration configFile;
    private final Configuration messagesFile;

    public HelpopCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.helpOpManager = pluginService.getPlayerManager().getHelpOpManager();

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onSubCommand(@Sender Player sender, @OptArg("") @Text String args) {

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("helpop", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, messagesFile.getString("helpop.received")
                .replace("%player%", sender.getName()));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop");

        String message = String.join(" ", args);

        if (configFile.getBoolean("modules.helpop.enable-revisor")) {
            TextRevisorEvent textrevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT,"Receive");
            Bukkit.getServer().getPluginManager().callEvent(textrevisorEvent);

            if (textrevisorEvent.isCancelled()){
                return true;
            }
        }

        Bukkit.getPluginManager().callEvent(new HelpOpEvent(sender, message));
        return true;
    }

    @Command(names = "-list")
    public boolean onListSubCommand(@Sender Player sender) {

        List<String> helpopList = new ArrayList<>();

        for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {

            UserData onlineCache = pluginService.getCache().getUserDatas().get(playeronline.getUniqueId());

            if (senderManager.hasPermission(sender, "helpop", "watch") && onlineCache.isPlayerHelpOp()) {
                helpopList.add(playeronline.getName());
            }
        }

        if (helpopList.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("helpop.error.anybody"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, messagesFile.getString("helpop.list-helpop"));

        for (String helpopPlayers : helpopList) {
            senderManager.sendMessage(sender, "&8- &f" + helpopPlayers);
        }

        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -list");
        return true;
    }

    @Command(names = "-on")
    public boolean onOnSubCommand(@Sender Player sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(senderManager.hasPermission(sender, "helpop", "watch"))) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (userData.isPlayerHelpOp()) {
            senderManager.sendMessage(sender, messagesFile.getString("helpop.error.activated"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        helpOpManager.enableOption(sender.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("helpop.player.enabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -on");
        return true;
    }

    @Command(names = "-off")
    public boolean onOffSubCommand(@Sender Player sender) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(senderManager.hasPermission(sender, "helpop", "watch"))) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(userData.isPlayerHelpOp())) {
            senderManager.sendMessage(sender, messagesFile.getString("helpop.error.unactivated"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        helpOpManager.disableOption(sender.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("helpop.player.disabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -off");
        return true;
    }

    @Command(names = "-toggle")
    public boolean onToggleSubCommand(@Sender Player sender) {

        if (!(senderManager.hasPermission(sender, "helpop", "watch"))) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
            return true;
        }

        helpOpManager.toggleOption(sender.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("helpop.player.toggle")
                .replace("%mode%", helpOpManager.getStatus()));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "helpop -toggle");
        return true;
    }
}
