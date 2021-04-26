package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.managers.sound.SoundManager;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.managers.group.GroupMethod;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
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

    private final Configuration utils;
    private final Configuration messages;
    private final Configuration command;

    private final SenderManager playerMethod;

    private final GroupMethod groupChannel;

    public ChannelCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messages = pluginService.getFiles().getMessages();
        this.command = pluginService.getFiles().getCommand();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.playerMethod = pluginService.getPlayerManager().getSender();
        this.groupChannel = pluginService.getPlayerManager().getGroupMethod();
    }

    @Command(names = {""})
    public boolean mainCommand(@Sender Player player) {

        playerMethod.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("channel", "join, quit, list, info, move")));
        playerMethod.sendSound(player, SoundEnum.ERROR);
        return true;

    }

    @Command(names = {"join"})
    public boolean joinSubCommand(@Sender Player sender, @OptArg("") String args) {

        UUID playeruuid = sender.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(playeruuid);

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("channel", "join", "<channel>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (utils.getString("channel." + args) == null) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.no-exists"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }


        if (userData.equalsChannelGroup(args)) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.joined")
                    .replace("%channel%", userData.getChannelGroup()));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(groupChannel.hasGroupPermission(sender, args))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.channel.player.left")
                .replace("%beforechannel%", userData.getChannelGroup())
                .replace("%afterchannel%", args));

        userData.setChannelGroup(args);

        if (args.equalsIgnoreCase("default")) {
            userData.setPlayerChannel(GroupEnum.GLOBAL);
        } else {
            userData.setPlayerChannel(GroupEnum.CHANNEL);
        }

        playerMethod.sendMessage(sender, command.getString("commands.channel.player.join")
                .replace("%channel%", userData.getChannelGroup()));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "channel join");
        return true;
    }

    @Command(names = {"quit"})
    public boolean quitSubCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(playeruuid);

        if (userData.equalsChannelGroup("default")) {
            playerMethod.sendMessage(player, messages.getString("error.channel.default.quit"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        String beforechannel = userData.getChannelGroup();
        userData.setChannelGroup("default");
        userData.setPlayerChannel(GroupEnum.GLOBAL);

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

        for (String group : utils.getConfigurationSection("channel").getKeys(false)){
            playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%channel%", group));
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
                    .replace("%usage%", TextUtils.getUsage("channel", "move", "<player>", "<channel>")));
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
                    .replace("%usage%", TextUtils.getUsage("channel", "move", "<player>", "<channel>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (utils.getString("channel." + groupChannel) == null) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.no-exists"));
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

        if (channel.equalsIgnoreCase("default")) {
            userData.setPlayerChannel(GroupEnum.GLOBAL);
        } else {
            userData.setPlayerChannel(GroupEnum.CHANNEL);
        }

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

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (channel.isEmpty()) {
            channel = userData.getChannelGroup();
        }

        if (userData.equalsChannelGroup(channel)) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.default.info"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (utils.getString("channel." + channel) == null) {
            playerMethod.sendMessage(sender, messages.getString("error.channel.no-exists"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        int online = 0;

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            UserData onlineData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (!onlineData.equalsChannelGroup(channel)) {
                break;
            }

            online++;
        }

        String condition;
        switch (utils.getString("channel." + channel + ".condition.type").toLowerCase()){
            case "group":
                condition = command.getString("commands.channel.info.condition.perms");
                break;
            case "permission":
                condition = command.getString("commands.channel.info.condition.group");
                break;
            default:
                return true;

        }
        String conditionAns = utils.getString("channel." + channel + ".condition.format").toLowerCase();

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