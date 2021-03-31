package code.commands;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.bukkitutils.sound.SoundManager;
import code.data.UserData;
import code.managers.GroupMethod;
import code.managers.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(names = {"channel", "chn"})
public class ChannelCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration messages;
    private final Configuration command;

    private final PlayerMessage playerMethod;
    private final SoundManager sound;

    private final GroupMethod groupChannel;
    private final ModuleCheck moduleCheck;

    public ChannelCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messages = pluginService.getFiles().getMessages();
        this.command = pluginService.getFiles().getCommand();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.sound = pluginService.getManagingCenter().getSoundManager();

        this.groupChannel = pluginService.getPlayerMethods().getGroupMethod();
        this.moduleCheck = pluginService.getPathManager();
    }

    @Command(names = {""})
    public boolean mainCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();

        playerMethod.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("channel", "join, quit, list, info, move")));
        playerMethod.sendSound(player, SoundEnum.ERROR);
        return true;

    }

    @Command(names = {"join"})
    public boolean joinSubCommand(@Sender Player player, @OptArg String args) {

        UUID playeruuid = player.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(playeruuid);

        if (args == null) {
            playerMethod.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("channel", "join", "<channel>")));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (groupChannel.channelNotExists(args)) {
            playerMethod.sendMessage(player, messages.getString("error.channel.no-exists"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!(groupChannel.isChannelEnabled(args))) {
            playerMethod.sendMessage(player, messages.getString("error.channel.disabled"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }


        if (userData.equalsChannelGroup(args)) {
            playerMethod.sendMessage(player, messages.getString("error.channel.joined")
                    .replace("%channel%", userData.getChannelGroup()));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!(groupChannel.hasGroupPermission(player, args))) {
            playerMethod.sendMessage(player, messages.getString("error.no-perms"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(player, command.getString("commands.channel.player.left")
                .replace("%beforechannel%", userData.getChannelGroup())
                .replace("%afterchannel%", args));

        userData.setChannelGroup(args);
        playerMethod.sendMessage(player, command.getString("commands.channel.player.join")
                .replace("%channel%", userData.getChannelGroup()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "channel join");
        return true;
    }

    @Command(names = {"quit"})
    public boolean quitSubCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(playeruuid);

        if (userData.equalsChannelGroup("default")) {
            playerMethod.sendMessage(player, messages.getString("error.channel.default"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        String beforechannel = userData.getChannelGroup();
        userData.setChannelGroup("default");

        playerMethod.sendMessage(player, command.getString("commands.channel.player.left")
                .replace("%beforechannel%", beforechannel)
                .replace("%afterchannel%", userData.getChannelGroup()));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "channel quit");
        return true;
    }


    @Command(names = {"list"})
    public boolean listSubCommand(@Sender Player player) {

        playerMethod.sendMessage(player, command.getString("commands.channel.list.message"));
        playerMethod.sendMessage(player, command.getString("commands.channel.list.space"));

        playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                .replace("%channel%", "default")
                .replace("%mode%", "&e[Default]"));

        for (String group : groupChannel.getGroup()) {
            if (groupChannel.isChannelEnabled(group)) {
                playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%channel%", group)
                        .replace("%mode%", "&a[Enabled]"));
            } else {
                playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%channel%", group)
                        .replace("%mode%", "&c[Disabled]"));
            }
        }

        playerMethod.sendMessage(player, command.getString("commands.channel.list.space"));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "channel list");
        return true;
    }

    @Command(names = {"move"})
    public boolean moveSubCommand(@Sender Player sender, @OptArg OfflinePlayer target, @OptArg("") String channel) {

        if (!playerMethod.hasPermission(sender, "commands.channel.move")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target == null) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("channel", "move", "<player>", "<channel>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(target.isOnline())) {
            playerMethod.sendMessage(sender, messages.getString("error.player-offline"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        UUID targetuuid = target.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(targetuuid);

        if (channel.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("channel", "move", "<player>", "<channel>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (groupChannel.channelNotExists(channel)) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.no-exists"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(groupChannel.isChannelEnabled(channel))) {
            playerMethod.sendMessage(target.getPlayer(), messages.getString("error.channel.disabled"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (userData.equalsChannelGroup(channel)) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.arg2-joined")
                    .replace("%arg-2%", target.getName())
                    .replace("%channel%", userData.getChannelGroup()));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.channel.player.move.sender")
                .replace("%arg-2%", target.getName())
                .replace("%channel%", channel));

        userData.setChannelGroup(channel);
        playerMethod.sendMessage(target.getPlayer(), command.getString("commands.channel.player.move.target")
                .replace("%channel%", channel));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "channel move");
        return true;
    }

    @Command(names = {"info"})
    public boolean infoSubCommand(@Sender Player sender, @OptArg("") String channel) {

        if (!playerMethod.hasPermission(sender, "commands.channel.info")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        Configuration utils = pluginService.getFiles().getBasicUtils();


        if (channel.isEmpty()) {
            channel = groupChannel.getPlayerGroup(sender);
        }

        if (groupChannel.channelNotExists(channel)) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.no-exists"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(groupChannel.isChannelEnabled(channel))) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.disabled"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        int online = 0;

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            UserData userData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (!userData.equalsChannelGroup(channel)) {
                break;
            }

            online++;
        }

        String condition;
        String conditionAns;

        if (utils.getString("chat.format.group-access").equalsIgnoreCase("group")) {
            condition = command.getString("commands.channel.info.condition.group");
            conditionAns = channel;
        } else {
            condition = command.getString("commands.channel.info.condition.perms");
            conditionAns = groupChannel.getGroupPermission(channel);
        }

        String status;
        if (groupChannel.hasGroupPermission(sender, channel)) {
            status = command.getString("commands.channel.info.status.yes_permission");
        } else {
            status = command.getString("commands.channel.info.status.no_permission");
        }

        for (String path : command.getStringList("commands.channel.info.format")) {
            playerMethod.sendMessage(sender, path
                    .replace("%online_channel%", String.valueOf(online))
                    .replace("%channel%", channel)
                    .replace("%condition%", condition)
                    .replace("%condition_ans%", conditionAns)
                    .replace("%status%", status));
        }
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "channel info");
        return true;
    }
}