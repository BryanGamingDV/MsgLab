package code.commands;

import code.data.UserData;
import code.events.SocialSpyEvent;
import code.methods.player.PlayerStatic;
import code.registry.ConfigManager;
import code.PluginService;
import code.methods.player.PlayerMessage;
import code.bukkitutils.SoundCreator;
import code.revisor.RevisorManager;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import code.utils.Configuration;

import java.util.List;
import java.util.UUID;

public class ReplyCommand implements CommandClass {

    private final PluginService pluginService;

    public ReplyCommand(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @Command(names = {"reply", "r"})
    public boolean onCommand(@Sender Player sender, @OptArg("") @Text String message) {

        ConfigManager files = pluginService.getFiles();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration players = files.getPlayers();
        Configuration command = files.getCommand();
        Configuration lang = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (message.isEmpty()) {
            playerMethod.sendMessage(sender, lang.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "reply",  "<message>")));
            sound.setSound(sender.getUniqueId(), "sounds.error");

            return true;
        }

        UserData playerCache = pluginService.getCache().getPlayerUUID().get(sender.getUniqueId());

        if (!playerCache.hasRepliedPlayer()) {
            playerMethod.sendMessage(sender, lang.getString("error.no-reply"));
            sound.setSound(sender.getUniqueId(), "sounds.error");
            return true;
        }

        Player target = Bukkit.getPlayer(playerCache.getRepliedPlayer());

        if (message.equalsIgnoreCase("-sender")){
            playerMethod.sendMessage(sender, command.getString("commands.msg-reply.talked")
                    .replace("%sender%", target.getName()));
            sound.setSound(sender.getUniqueId(), "sounds.on-reply-sender");
            return true;
        }

        CommandSender targetsender = target.getPlayer();

        if (command.getBoolean("commands.msg-reply.enable-revisor")){
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        if (playerMethod.hasPermission(sender, "color.commands")){
            message = PlayerStatic.setColor(message);
        }

        playerMethod.sendMessage(sender, PlayerStatic.setColor(command.getString("commands.msg-reply.sender")
                    .replace("%sender%", sender.getName())
                    .replace("%arg-1%", target.getName()))
                    , message);
        sound.setSound(sender.getUniqueId(), "sounds.on-reply");

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (!(ignoredlist.contains(target.getName()))) {
            playerMethod.sendMessage(targetsender, PlayerStatic.setColor(command.getString("commands.msg-reply.sender")
                            .replace("%sender%", sender.getName())
                            .replace("%arg-1%", target.getName()))
                            , message);

            UserData targetCache = pluginService.getCache().getPlayerUUID().get(target.getUniqueId());

            targetCache.setRepliedPlayer(playeruuid);
            sound.setSound(sender.getUniqueId(), "sounds.on-reply");
        }

        String socialspyFormat = command.getString("commands.socialspy.spy")
                .replace("%sender%", sender.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", message);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));
        return true;
    }
}


