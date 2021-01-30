package code.commands;

import code.CacheManager;
import code.cache.UserData;
import code.events.SocialSpyEvent;
import code.methods.player.PlayerStatic;
import code.registry.ConfigManager;
import code.Manager;
import code.methods.player.PlayerMessage;
import code.bukkitutils.SoundManager;
import code.revisor.RevisorManager;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import code.utils.Configuration;

import java.util.List;
import java.util.UUID;

public class ReplyCommand implements CommandClass {

    private final Manager manager;

    public ReplyCommand(Manager manager){
        this.manager = manager;
    }

    @Command(names = {"reply", "r"})
    public boolean onCommand(@Sender Player player, @OptArg("") @Text String message) {

        ConfigManager files = manager.getFiles();
        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        SoundManager sound = manager.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = manager.getPathManager();

        Configuration players = files.getPlayers();
        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration lang = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (message.isEmpty()) {
            playersender.sendMessage(player, lang.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "reply",  "<message>")));
            sound.setSound(player.getUniqueId(), "sounds.error");

            return true;
        }

        UserData playerCache = manager.getCache().getPlayerUUID().get(player.getUniqueId());

        if (!playerCache.hasRepliedPlayer()) {
            playersender.sendMessage(player, lang.getString("error.no-reply"));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        Player target = Bukkit.getPlayer(playerCache.getRepliedPlayer());

        if (message.equalsIgnoreCase("-player")){
            playersender.sendMessage(player, command.getString("commands.msg-reply.talked")
                    .replace("%player%", target.getName()));
            sound.setSound(player.getUniqueId(), "sounds.on-reply-player");
            return true;
        }

        CommandSender targetsender = target.getPlayer();

        if (command.getBoolean("commands.msg-reply.enable-revisor")){
            RevisorManager revisorManager = manager.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        if (player.hasPermission(config.getString("config.perms.msg-color"))){
            message = PlayerStatic.setColor(message);
        }

        playersender.sendMessage(player, PlayerStatic.setColor(command.getString("commands.msg-reply.player")
                    .replace("%player%", player.getName())
                    .replace("%arg-1%", target.getName()))
                    , message);
        sound.setSound(player.getUniqueId(), "sounds.on-reply");

        List<String> ignoredlist = players.getStringList("players." + playeruuid + ".players-ignored");

        if (!(ignoredlist.contains(target.getName()))) {
            playersender.sendMessage(targetsender, PlayerStatic.setColor(command.getString("commands.msg-reply.player")
                            .replace("%player%", player.getName())
                            .replace("%arg-1%", target.getName()))
                            , message);

            UserData targetCache = manager.getCache().getPlayerUUID().get(target.getUniqueId());

            targetCache.setRepliedPlayer(playeruuid);
            sound.setSound(player.getUniqueId(), "sounds.on-reply");
        }

        String socialspyFormat = command.getString("commands.socialspy.spy")
                .replace("%player%", player.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", message);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));
        return true;
    }
}


