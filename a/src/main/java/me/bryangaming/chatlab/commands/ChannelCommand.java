package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.managers.group.GroupManager;
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

import java.util.UUID;

@Command(names = {"channel", "chn"})

public class ChannelCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration formatsFile;
    private final Configuration messagesFile;
    private final Configuration commandFile;

    private final SenderManager senderManager;

    private final GroupManager groupChannel;

    public ChannelCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messagesFile = pluginService.getFiles().getMessagesFile();
        this.commandFile = pluginService.getFiles().getCommandFile();
        this.formatsFile = pluginService.getFiles().getFormatsFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.groupChannel = pluginService.getPlayerManager().getGroupManager();
    }

    @Command(names = {""})
    public boolean mainCommand(@Sender Player player) {

        senderManager.sendMessage(player, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("channel", "join, quit, list, info, move")));
        senderManager.playSound(player, SoundEnum.ERROR);
        return true;

    }

    @Command(names = {"join"})
    public boolean joinSubCommand(@Sender Player sender, @OptArg("") String args) {

        UUID playeruuid = sender.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(playeruuid);

        if (args.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("channel", "join", "<channel>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (formatsFile.getString("channel." + args) == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.channel.no-exists"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }


        if (userData.equalsChannelGroup(args)) {
            senderManager.sendMessage(sender, messagesFile.getString("error.channel.joined")
                    .replace("%channel%", userData.getChannelGroup()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(groupChannel.hasGroupPermission(sender, args))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.channel.player.left")
                .replace("%beforechannel%", userData.getChannelGroup())
                .replace("%afterchannel%", args));

        userData.setChannelGroup(args);

        if (args.equalsIgnoreCase("default")) {
            userData.setPlayerChannel(GroupEnum.GLOBAL);
        } else {
            userData.setPlayerChannel(GroupEnum.CHANNEL);
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.channel.player.join")
                .replace("%channel%", userData.getChannelGroup()));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "channel join");
        return true;
    }

    @Command(names = {"quit"})
    public boolean quitSubCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(playeruuid);

        if (userData.equalsChannelGroup("default")) {
            senderManager.sendMessage(player, messagesFile.getString("error.channel.default.quit"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        String beforechannel = userData.getChannelGroup();
        userData.setChannelGroup("default");
        userData.setPlayerChannel(GroupEnum.GLOBAL);

        senderManager.sendMessage(player, commandFile.getString("commands.channel.player.left")
                .replace("%beforechannel%", beforechannel)
                .replace("%afterchannel%", userData.getChannelGroup()));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "channel quit");
        return true;
    }


    @Command(names = {"list"})
    public boolean listSubCommand(@Sender Player player) {

        senderManager.sendMessage(player, commandFile.getString("commands.channel.list.message"));
        senderManager.sendMessage(player, commandFile.getString("commands.channel.list.space"));

        for (String group : formatsFile.getConfigurationSection("channel").getKeys(false)){
            senderManager.sendMessage(player, commandFile.getString("commands.channel.list.format")
                        .replace("%channel%", group));
        }
        senderManager.sendMessage(player, commandFile.getString("commands.channel.list.space"));
        senderManager.playSound(player, SoundEnum.ARGUMENT, "channel list");
        return true;
    }

    @Command(names = {"move"})
    public boolean moveSubCommand(@Sender Player sender, @OptArg OfflinePlayer target, @OptArg("") String channel) {

        if (!senderManager.hasPermission(sender, "commands.channel.move")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("channel", "move", "<player>", "<channel>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (!(target.isOnline())) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(target)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UUID targetuuid = target.getUniqueId();
        UserData userData = pluginService.getCache().getUserDatas().get(targetuuid);

        if (channel.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("channel", "move", "<player>", "<channel>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (formatsFile.getString("channel." + groupChannel) == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.channel.no-exists"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (userData.equalsChannelGroup(channel)) {
            senderManager.sendMessage(sender, messagesFile.getString("error.channel.arg2-joined")
                    .replace("%arg-2%", target.getName())
                    .replace("%channel%", userData.getChannelGroup()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.channel.player.move.sender")
                .replace("%arg-2%", target.getName())
                .replace("%channel%", channel));

        userData.setChannelGroup(channel);

        if (channel.equalsIgnoreCase("default")) {
            userData.setPlayerChannel(GroupEnum.GLOBAL);
        } else {
            userData.setPlayerChannel(GroupEnum.CHANNEL);
        }

        senderManager.sendMessage(target.getPlayer(), commandFile.getString("commands.channel.player.move.target")
                .replace("%channel%", channel));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "channel move");
        return true;
    }

    @Command(names = {"info"})
    public boolean infoSubCommand(@Sender Player sender, @OptArg("") String channel) {

        if (!senderManager.hasPermission(sender, "commands.channel.info")) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (channel.isEmpty()) {
            channel = userData.getChannelGroup();
        }

        if (userData.equalsChannelGroup(channel)) {
            senderManager.sendMessage(sender, messagesFile.getString("error.channel.default.info"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (formatsFile.getString("channel." + channel) == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.channel.no-exists"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        int online = 0;

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            UserData onlineData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

            if (pluginService.getSupportManager().getVanishSupport().isVanished(onlinePlayer)){
                senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                continue;
            }

            if (!onlineData.equalsChannelGroup(channel)) {
                continue;
            }

            online++;
        }

        String condition;
        switch (formatsFile.getString("channel." + channel + ".condition.type").toLowerCase()){
            case "group":
                condition = commandFile.getString("commands.channel.info.condition.perms");
                break;
            case "permission":
                condition = commandFile.getString("commands.channel.info.condition.group");
                break;
            default:
                return true;

        }
        String conditionAns = formatsFile.getString("channel." + channel + ".condition.format").toLowerCase();

        String status;
        if (groupChannel.hasGroupPermission(sender, channel)) {
            status = commandFile.getString("commands.channel.info.status.yes_permission");
        } else {
            status = commandFile.getString("commands.channel.info.status.no_permission");
        }

        for (String path : commandFile.getStringList("commands.channel.info.format")) {
            senderManager.sendMessage(sender, path
                    .replace("%online_channel%", String.valueOf(online))
                    .replace("%channel%", channel)
                    .replace("%condition%", condition)
                    .replace("%condition_ans%", conditionAns)
                    .replace("%status%", status));
        }
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "channel info");
        return true;
    }
}