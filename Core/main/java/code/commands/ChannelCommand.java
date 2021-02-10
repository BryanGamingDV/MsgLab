package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.data.UserData;
import code.methods.GroupMethod;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(names = {"channel", "chn"})
public class ChannelCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration messages;
    private final Configuration command;

    private final PlayerMessage playerMethod;
    private final SoundCreator sound;

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
                .replace("%usage%", moduleCheck.getUsage("channel", "join, quit, list")));
        sound.setSound(playeruuid, "sounds.error");
        return true;

    }

    @Command(names = {"join"})
    public boolean joinSubCommand(@Sender Player player, @OptArg String args){

        UUID playeruuid = player.getUniqueId();
        UserData userData = pluginService.getCache().getPlayerUUID().get(playeruuid);

        if (args == null){
            playerMethod.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("channel" , "join", "<channel>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (groupChannel.channelNotExists(args)){
            playerMethod.sendMessage(player, messages.getString("error.channel.no-exists"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (!(groupChannel.isChannelEnabled(args))){
            playerMethod.sendMessage(player, messages.getString("error.channel.disabled"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }


        if (userData.equalsChannelGroup(args)){
            playerMethod.sendMessage(player, messages.getString("error.channel.joined")
                    .replace("%rank%", userData.getChannelGroup()));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (!(groupChannel.hasGroupPermission(player, args))){
            playerMethod.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        playerMethod.sendMessage(player, command.getString("commands.channel.player.left")
                .replace("%beforechannel%", userData.getChannelGroup())
                .replace("%afterchannel%", args));

        userData.setChannelGroup(args);
        playerMethod.sendMessage(player, command.getString("commands.channel.player.join")
                .replace("%channel%", userData.getChannelGroup()));
        return true;
    }

    @Command(names = {"quit"})
    public boolean quitSubCommand(@Sender Player player) {

        UUID playeruuid = player.getUniqueId();
        UserData userData = pluginService.getCache().getPlayerUUID().get(playeruuid);

        if (userData.equalsChannelGroup("default")) {
            playerMethod.sendMessage(player, messages.getString("error.channel.default"));
            return true;
        }

        playerMethod.sendMessage(player, command.getString("commands.channel.player.left")
                .replace("%channel%", userData.getChannelGroup()));
        userData.setChannelGroup("default");
        return true;
    }


    @Command(names = {"list"})
    public boolean listSubCommand(@Sender Player player) {

        playerMethod.sendMessage(player, command.getString("commands.channel.list.message"));
        playerMethod.sendMessage(player, command.getString("commands.channel.list.space"));

        for (String group : groupChannel.getGroup()) {
            if (group.equalsIgnoreCase("default")) {
                playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%group%", group)
                        .replace("%mode%", "&e[Default]"));
                continue;
            }
            if (groupChannel.isChannelEnabled(group)) {
                playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%group%", group)
                        .replace("%mode%", "&a[Enabled]"));
            } else {
                playerMethod.sendMessage(player, command.getString("commands.channel.list.format")
                        .replace("%group%", group)
                        .replace("%mode%", "&c[Disabled]"));
            }
        }
        playerMethod.sendMessage(player, command.getString("commands.channel.list.space"));
        return true;
    }
}
