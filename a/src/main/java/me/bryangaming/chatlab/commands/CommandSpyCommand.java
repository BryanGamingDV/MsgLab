package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(names = {"commandspy", "cspy"})
public class CommandSpyCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration commandFile;
    private final Configuration messagesFile;

    private final SenderManager senderManager;

    public CommandSpyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = {""})
    public boolean onMainSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("cspy", "on, off, list, block, unblock")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = {"on"})
    public boolean onOnSubCommand(@Sender Player sender, @OptArg OfflinePlayer offlinePlayer) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (offlinePlayer == null) {

            if (userData.isCommandspyMode()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.status.player.already-enabled"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            userData.setCommandspyMode(true);
            senderManager.sendMessage(sender, commandFile.getString("commands.commandspy.player.enabled"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy on");
            return true;
        }

        if (!offlinePlayer.isOnline()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }


        UserData targetData = pluginService.getCache().getUserDatas().get(offlinePlayer.getUniqueId());


        if (targetData.isCommandspyMode()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.status.arg-2.already-enabled")
                    .replace("%arg-2%", offlinePlayer.getName()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        targetData.setCommandspyMode(true);
        senderManager.sendMessage(sender, commandFile.getString("commands.commandspy.arg-2.enabled")
                .replace("%arg-2%", offlinePlayer.getName()));
        senderManager.sendMessage(targetData.getPlayer(), commandFile.getString("commands.commandspy.player.enabled")
                .replace("%player%", offlinePlayer.getName()));
        senderManager.playSound(offlinePlayer.getPlayer(), SoundEnum.ARGUMENT, "commandspy on");
        return true;
    }

    @Command(names = {"off"})
    public boolean onOffSubCommand(@Sender Player sender, @OptArg OfflinePlayer offlinePlayer) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (offlinePlayer == null) {

            if (!userData.isCommandspyMode()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.status.already-disabled"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            userData.setCommandspyMode(false);
            senderManager.sendMessage(sender, commandFile.getString("commands.commandspy.player.disabled"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy off");
            return true;
        }

        if (!offlinePlayer.isOnline()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData targetData = pluginService.getCache().getUserDatas().get(offlinePlayer.getUniqueId());

        if (!userData.isCommandspyMode()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.status.arg-2.already-disabled")
                    .replace("%arg-2%", offlinePlayer.getName()));
            return true;
        }

        targetData.setCommandspyMode(false);
        senderManager.sendMessage(sender, commandFile.getString("commands.commandspy.arg-2.disabled")
                .replace("%arg-2%", offlinePlayer.getName()));
        senderManager.sendMessage(targetData.getPlayer(), commandFile.getString("commands.commandspy.player.disabled"));
        senderManager.playSound(offlinePlayer.getPlayer(), SoundEnum.ARGUMENT, "commandspy off");
        return true;
    }

    @Command(names = {"list"})
    public boolean onListSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, commandFile.getStringList("commands.commandspy.list.format"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy list");
            return true;
        }

        if (args.equalsIgnoreCase("players")) {

            List<Player> playerList = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                UserData onlineData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

                if (!onlineData.isCommandspyMode()) {
                    continue;

                }
                playerList.add(sender);
            }

            if (playerList.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.list.players.empty"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            for (String message : commandFile.getStringList("commands.commandspy.list.players.format")) {
                senderManager.sendMessage(sender, message);

                if (message.contains("%loop-value%")) {

                    for (Player onlinePlayer : playerList) {
                        senderManager.sendMessage(sender, message
                                .replace("%loop-value%", onlinePlayer.getName()));

                    }
                }

                senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy list players");
            }
            return true;
        }

        if (args.equalsIgnoreCase("blocked-commands")) {

            List<String> blockedCommands = commandFile.getStringList("commands.commandspy.blocked-commands");

            if (blockedCommands.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.list.blocked-commands.empty"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            for (String message : commandFile.getStringList("commands.commandspy.list.blocked-commands.format")) {

                if (message.contains("%loop-value%")) {
                    for (String blockedCommand : blockedCommands) {
                        senderManager.sendMessage(sender, message
                                .replace("%loop-value%", blockedCommand));
                    }
                    continue;
                }

                senderManager.sendMessage(sender, message);
                senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy list blocked-commands");
            }
            return true;
        }
        return true;
    }

    @Command(names = {"block"})
    public boolean onBlockSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("cspy", "block", "<word>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> blockedWords = commandFile.getStringList("commands.commandspy.blocked-commands");

        if (blockedWords.contains(args)) {
            senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.commands.already-blocked")
                    .replace("%command%", args));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        blockedWords.add(args);
        commandFile.set("commands.commandspy.blocked-commands", blockedWords);
        commandFile.save();

        senderManager.sendMessage(sender, commandFile.getString("commands.commandspy.commands.blocked")
                .replace("%command%", args));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy block");
        return true;
    }

    @Command(names = {"unblock"})
    public boolean onUnBlockSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("cspy", "block", "<word>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> blockedWords = commandFile.getStringList("commands.commandspy.blocked-commands");

        if (!blockedWords.contains(args)) {
            senderManager.sendMessage(sender, messagesFile.getString("error.commandspy.commands.already-unblocked")
                    .replace("%command%", args));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        blockedWords.remove(args);

        commandFile.set("commands.commandspy.blocked-commands", blockedWords);
        commandFile.save();

        senderManager.sendMessage(sender, commandFile.getString("commands.commandspy.commands.unblocked")
                .replace("%command%", args));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "commandspy unblock");
        return true;
    }


}
