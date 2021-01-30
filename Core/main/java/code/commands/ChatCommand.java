package code.commands;

import code.BasicMsg;
import code.PluginService;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.bukkitutils.SoundManager;
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
    private final BasicMsg plugin;

    private final Configuration config;
    private final Configuration command;
    private final Configuration messages;
    private final Configuration utils;

    private final PlayerMessage playersender;

    public ChatCommand(BasicMsg plugin, PluginService pluginService){
        this.pluginService = pluginService;
        this.plugin = plugin;

        this.config = pluginService.getFiles().getConfig();
        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.playersender = pluginService.getPlayerMethods().getSender();
    }

    @Command(names = "")
    public boolean mainSubCommand(@Sender Player player) {

        ModuleCheck moduleCheck = pluginService.getPathManager();
        SoundManager sound = pluginService.getManagingCenter().getSoundManager();

        UUID playeruuid = player.getUniqueId();

        if (!(utils.getBoolean("utils.chat.enabled"))) {
            playersender.sendMessage(player, messages.getString("error.option-disabled")
                    .replace("%player%", player.getName())
                    .replace("%option%", "ChatManagement"));
            return true;
        }

        playersender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage( "chat", "help, reload")));
        sound.setSound(playeruuid, "sounds.error");
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player player) {

        StringFormat variable = pluginService.getStringFormat();
        variable.loopString(player, command, "commands.chat.help");
        return true;

    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player player) {
        if (!(player.hasPermission(config.getString("config.perms.chat-reload")))) {
            playersender.sendMessage(player, messages.getString("error.no-perms"));
            return true;

        }
        playersender.sendMessage(player, command.getString("commands.bmsg.load"));
        this.getReloadEvent(player);
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
