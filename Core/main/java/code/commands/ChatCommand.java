package code.commands;

import code.MsgLab;
import code.PluginService;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.bukkitutils.SoundCreator;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import code.utils.StringFormat;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


@Command(names = {"chat"})
public class ChatCommand implements CommandClass{

    private final PluginService pluginService;
    private final MsgLab plugin;

    private final Configuration command;
    private final Configuration messages;
    private final Configuration utils;

    private final PlayerMessage playerMethod;

    public ChatCommand(MsgLab plugin, PluginService pluginService){
        this.pluginService = pluginService;
        this.plugin = plugin;

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.playerMethod = pluginService.getPlayerMethods().getSender();
    }

    @Command(names = "")
    public boolean mainSubCommand(@Sender Player sender) {

        ModuleCheck moduleCheck = pluginService.getPathManager();
        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();

        UUID playeruuid = sender.getUniqueId();

        if (!(utils.getBoolean("chat.enabled"))) {
            playerMethod.sendMessage(sender, messages.getString("error.option-disabled")
                    .replace("%sender%", sender.getName())
                    .replace("%option%", "ChatManagement"));
            return true;
        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage( "chat", "help, reload")));
        sound.setSound(playeruuid, "sounds.error");
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        StringFormat variable = pluginService.getStringFormat();
        variable.loopString(sender, command, "commands.chat.help");
        return true;

    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender) {

        if (!playerMethod.hasPermission(sender, "commands.chat.reload")){
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;

        }

        playerMethod.sendMessage(sender, command.getString("commands.bmsg.load"));
        this.getReloadEvent(sender);
        return true;
    }

    public void getReloadEvent(Player sender){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

                ConfigManager files = pluginService.getFiles();
                files.getBasicUtils().reload();
                playersender.sendMessage(sender, files.getCommand().getString("commands.bmsg.reload"));
            }

        },20L * 3);
    }
}
