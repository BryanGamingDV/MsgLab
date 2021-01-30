package code.commands;

import code.Manager;
import code.bukkitutils.SoundManager;
import code.cache.UserData;
import code.methods.GroupMethod;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.ArgOrSub;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Required;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(names = {"channel", "chn"})
public class ChannelCommand implements CommandClass {

    private final Manager manager;

    private final Configuration messages;
    private final Configuration command;

    private final PlayerMessage sender;
    private final SoundManager sound;

    private final GroupMethod groupChannel;
    private final ModuleCheck moduleCheck;

    public ChannelCommand(Manager manager) {
        this.manager = manager;

        this.messages = manager.getFiles().getMessages();
        this.command = manager.getFiles().getCommand();

        this.sender = manager.getPlayerMethods().getSender();
        this.sound = manager.getManagingCenter().getSoundManager();

        this.groupChannel = manager.getPlayerMethods().getGroupMethod();
        this.moduleCheck = manager.getPathManager();
    }

    @Command(names = {""})
    public boolean mainCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();

        sender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("channel", "join, quit, list")));
        sound.setSound(playeruuid, "sounds.error");
        return true;

    }

    @Command(names = {"join"})
    public boolean joinSubCommand(@Sender Player player, @OptArg String args){

        UUID playeruuid = player.getUniqueId();
        UserData userData = manager.getCache().getPlayerUUID().get(playeruuid);

        if (args == null){
            sender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("channel" , "join", "<channel>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (groupChannel.channelNotExists(args)){
            sender.sendMessage(player, messages.getString("error.channel.no-exists"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (!(groupChannel.isChannelEnabled(args))){
            sender.sendMessage(player, messages.getString("error.channel.disabled"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }


        if (userData.equalsChannelGroup(args)){
            sender.sendMessage(player, messages.getString("error.channel.joined")
                    .replace("%rank%", userData.getChannelGroup()));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (!(groupChannel.hasGroupPermission(player, args))){
            sender.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        sender.sendMessage(player, command.getString("commands.channel.player.left")
                .replace("%beforechannel%", userData.getChannelGroup())
                .replace("%afterchannel%", args));

        userData.setChannelGroup(args);
        sender.sendMessage(player, command.getString("commands.channel.player.join")
                .replace("%channel%", userData.getChannelGroup()));
        return true;
    }

    @Command(names = {"quit"})
    public boolean quitSubCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();
        UserData userData = manager.getCache().getPlayerUUID().get(playeruuid);

        if (userData.equalsChannelGroup("default")) {
            sender.sendMessage(player, messages.getString("error.channel.default"));
            return true;
        }

        sender.sendMessage(player, command.getString("commands.channel.player.left")
                .replace("%channel%", userData.getChannelGroup()));
        userData.setChannelGroup("default");
        return true;
    }


    @Command(names = {"list"})
    public boolean listSubCommand(@Sender Player player) {

        sender.sendMessage(player, command.getString("commands.channel.list.message"));
        sender.sendMessage(player, command.getString("commands.channel.list.space"));

        for (String group : groupChannel.getGroup()) {
            if (group.equalsIgnoreCase("default")) {
                sender.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%group%", group)
                        .replace("%mode%", "&e[Default]"));
                continue;
            }
            if (groupChannel.isChannelEnabled(group)) {
                sender.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%group%", group)
                        .replace("%mode%", "&a[Enabled]"));
            } else {
                sender.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%group%", group)
                        .replace("%mode%", "&c[Disabled]"));
            }
        }
        sender.sendMessage(player, command.getString("commands.channel.list.space"));
        return true;
    }
}
