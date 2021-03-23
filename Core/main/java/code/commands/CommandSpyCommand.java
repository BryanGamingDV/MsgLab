package code.commands;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.data.UserData;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(names = {"commandspy", "cspy"})
public class CommandSpyCommand implements CommandClass {

    private final PluginService pluginService;

    private final PlayerMessage playerMethod;
    private final ModuleCheck moduleCheck;

    private Configuration command;
    private Configuration messages;

    public CommandSpyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.moduleCheck = pluginService.getPathManager();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = {""})
    public boolean onMainSubCommand(@Sender Player sender) {

        ModuleCheck moduleCheck = pluginService.getPathManager();

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("cspy", "on, off, list, block, unblock")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = {"on"})
    public boolean onOnSubCommand(@Sender Player sender, @OptArg OfflinePlayer offlinePlayer) {

        UserData userData = pluginService.getCache().getPlayerUUID().get(sender.getUniqueId());

        if (offlinePlayer == null) {

            if (userData.isCommandspyMode()){
                playerMethod.sendMessage(sender, messages.getString("error.commandspy.status.player.already-enabled"));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            userData.setCommandspyMode(true);
            playerMethod.sendMessage(sender, command.getString("commands.commandspy.player.enabled"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy on");
            return true;
        }

        if (!offlinePlayer.isOnline()) {
            playerMethod.sendMessage(sender, messages.getString("error.player-offline"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }


        UserData targetData = pluginService.getCache().getPlayerUUID().get(offlinePlayer.getUniqueId());


        if (targetData.isCommandspyMode()){
            playerMethod.sendMessage(sender, messages.getString("error.commandspy.status.arg-2.already-enabled")
                    .replace("%arg-2%", offlinePlayer.getName()));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        targetData.setCommandspyMode(true);
        playerMethod.sendMessage(sender, command.getString("commands.commandspy.arg-2.enabled")
                .replace("%arg-2%", offlinePlayer.getName()));
        playerMethod.sendMessage(targetData.getPlayer(), command.getString("commands.commandspy.player.enabled")
                    .replace("%player%", offlinePlayer.getName()));
        playerMethod.sendSound(offlinePlayer.getPlayer(), SoundEnum.ARGUMENT, "commandspy on");
        return true;
    }

    @Command(names = {"off"})
    public boolean onOffSubCommand(@Sender Player sender, @OptArg OfflinePlayer offlinePlayer) {

        UserData userData = pluginService.getCache().getPlayerUUID().get(sender.getUniqueId());

        if (offlinePlayer == null) {

            if (!userData.isCommandspyMode()){
                playerMethod.sendMessage(sender, messages.getString("error.commandspy.status.already-disabled"));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            userData.setCommandspyMode(false);
            playerMethod.sendMessage(sender, command.getString("commands.commandspy.player.disabled"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy off");
            return true;
        }

        if (!offlinePlayer.isOnline()) {
            playerMethod.sendMessage(sender, messages.getString("error.player-offline"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData targetData = pluginService.getCache().getPlayerUUID().get(offlinePlayer.getUniqueId());

        if (!userData.isCommandspyMode()){
            playerMethod.sendMessage(sender, messages.getString("error.commandspy.status.arg-2.already-disabled")
                    .replace("%arg-2%", offlinePlayer.getName()));
            return true;
        }

        targetData.setCommandspyMode(false);
        playerMethod.sendMessage(sender, command.getString("commands.commandspy.arg-2.disabled")
                .replace("%arg-2%", offlinePlayer.getName()));
        playerMethod.sendMessage(targetData.getPlayer(), command.getString("commands.commandspy.player.disabled"));
        playerMethod.sendSound(offlinePlayer.getPlayer(), SoundEnum.ARGUMENT, "commandspy off");
        return true;
    }

    @Command(names = {"list"})
    public boolean onListSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, command.getStringList("commands.commandspy.list.format"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy list");
            return true;
        }

        if (args.equalsIgnoreCase("players")) {

            List<Player> playerList = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                UserData onlineData = pluginService.getCache().getPlayerUUID().get(onlinePlayer.getUniqueId());

                if (!onlineData.isCommandspyMode()) {
                    continue;

                }
                playerList.add(sender);
            }

            if (playerList.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.commandspy.list.players.empty"));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            for (String message : command.getStringList("commands.commandspy.list.players.format")) {
                playerMethod.sendMessage(sender, message);

                if (message.contains("%loop-value%")) {

                    for (Player onlinePlayer : playerList){
                        playerMethod.sendMessage(sender, message
                                .replace("%loop-value%", onlinePlayer.getName()));

                    }
                }

                playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy list players");
            }
            return true;
        }

        if (args.equalsIgnoreCase("blocked-commands")) {

            List<String> blockedCommands = command.getStringList("commands.commandspy.blocked-commands");

            if (blockedCommands.isEmpty()){
                playerMethod.sendMessage(sender, messages.getString("error.commandspy.list.blocked-commands.empty"));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            for (String message : command.getStringList("commands.commandspy.list.blocked-commands.format")) {

                if (message.contains("%loop-value%")) {
                    for (String blockedCommand : blockedCommands) {
                        playerMethod.sendMessage(sender, message
                                .replace("%loop-value%", blockedCommand));
                    }
                    continue;
                }

                playerMethod.sendMessage(sender, message);
                playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy list blocked-commands");
            }
            return true;
        }
        return true;
    }

    @Command(names = {"block"})
    public boolean onBlockSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("cspy", "block", "<word>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> blockedWords = command.getStringList("commands.commandspy.blocked-commands");

        if (blockedWords.contains(args)){
            playerMethod.sendMessage(sender, messages.getString("error.commandspy.commands.already-blocked")
                    .replace("%command%", args));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        blockedWords.add(args);
        command.set("commands.commandspy.blocked-commands", blockedWords);
        command.save();

        playerMethod.sendMessage(sender, command.getString("commands.commandspy.commands.blocked")
                .replace("%command%", args));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy block");
        return true;
    }

    @Command(names = {"unblock"})
    public boolean onUnBlockSubCommand(@Sender Player sender, @OptArg("") String args) {

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("cspy", "block", "<word>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        List<String> blockedWords = command.getStringList("commands.commandspy.blocked-commands");

        if (!blockedWords.contains(args)){
            playerMethod.sendMessage(sender, messages.getString("error.commandspy.commands.already-unblocked")
                    .replace("%command%", args));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        blockedWords.remove(args);

        command.set("commands.commandspy.blocked-commands", blockedWords);
        command.save();

        playerMethod.sendMessage(sender, command.getString("commands.commandspy.commands.unblocked")
                .replace("%command%", args));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "commandspy unblock");
        return true;
    }


}
