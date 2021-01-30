package code.commands;

import code.CacheManager;
import code.bukkitutils.gui.manager.GuiManager;
import code.cache.UserData;
import code.events.SocialSpyEvent;
import code.methods.commands.MsgMethod;
import code.methods.commands.ReplyMethod;
import code.methods.player.PlayerStatic;
import code.registry.ConfigManager;
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
import code.Manager;

import java.util.UUID;

public class MsgCommand implements CommandClass{

    private final Manager manager;

    public MsgCommand(Manager manager){
        this.manager = manager;
    }

    @Command(names = {"msg", "pm", "tell", "t", "w", "whisper"})

    public boolean onCommand(@Sender Player player, @OptArg OfflinePlayer target, @OptArg("") @Text String msg) {

        ConfigManager files = manager.getFiles();
        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        SoundManager sound = manager.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = manager.getPathManager();

        Configuration config = files.getConfig();
        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = player.getUniqueId();

        if (target == null) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("msg", "<player>", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        UUID targetuuid = target.getUniqueId();

        if (target.getName().equalsIgnoreCase("-all")){
            playersender.sendMessage(player, "%p &fEmmm, use /broadcast or the chat.");
            playersender.sendMessage(player, "&8- EasterEgg #3");
            return true;
        }

        if (target.getName().equalsIgnoreCase("-online")) {
            if (Bukkit.getServer().getOnlinePlayers().size() < 2) {
                playersender.sendMessage(player, messages.getString("error.nobody-offline"));
                return true;
            }

            GuiManager guiManager = manager.getManagingCenter().getGuiManager();
            guiManager.openInventory(playeruuid, "online", 0);
            return true;
        }

        UserData playerMsgToggle = manager.getCache().getPlayerUUID().get(playeruuid);

        if (target.getName().equalsIgnoreCase("-toggle")) {
            if (msg.isEmpty()) {
                if (!(playerMsgToggle.isSocialSpyMode())) {
                    playerMsgToggle.toggleSocialSpy(true);
                    playersender.sendMessage(player, command.getString("commands.msg-toggle.player.activated"));
                    return true;
                }

                playerMsgToggle.toggleSocialSpy(false);
                playersender.sendMessage(player, command.getString("commands.msg-toggle.player.unactivated"));
                return true;
            }

            if (!(player.hasPermission(config.getString("config.perms.toggle-admin")))) {
                playersender.sendMessage(player, messages.getString("error.no-perms"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            Player you = Bukkit.getPlayer(msg);

            if (you == null) {
                playersender.sendMessage(player, messages.getString("error.player-offline"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            UserData targetMsgToggle = manager.getCache().getPlayerUUID().get(you.getUniqueId());

            if (!(targetMsgToggle.isMsgtoggleMode())) {
                targetMsgToggle.toggleMsg(true);
                playersender.sendMessage(player, command.getString("commands.msg-toggle.arg-1.activated")
                        .replace("%arg-1%", you.getName()));
                playersender.sendMessage(you, command.getString("commands.msg-toggle.player.activated"));

            } else {
                targetMsgToggle.toggleMsg(false);
                playersender.sendMessage(player, command.getString("commands.msg-toggle.arg-1.unactivated")
                        .replace("%arg-1%", you.getName()));
                playersender.sendMessage(you, command.getString("commands.msg-toggle.player.unactivated"));
            }
            sound.setSound(player.getUniqueId(), "sounds.on-togglepm");
            return true;
        }


        if (!(target.isOnline())) {
            playersender.sendMessage(player, messages.getString("error.player-offline"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (target.getName().equalsIgnoreCase(player.getName())) {
            playersender.sendMessage(player, messages.getString("error.same-player")
                    .replace("%player%", player.getName()));
            sound.setSound(playeruuid, "sounds.error");
            return true;

        }

        UserData targetToggled = manager.getCache().getPlayerUUID().get(targetuuid);

        if (targetToggled == null) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("msg", "<player>", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (targetToggled.isMsgtoggleMode()) {
            playersender.sendMessage(player, command.getString("commands.msg-toggle.msg")
                    .replace("%player%", target.getName()));
            return true;
        }


        if (msg.isEmpty()) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("msg", "<player>", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        String message = String.join(" ", msg);

        if (command.getBoolean("commands.msg-reply.enable-revisor")){
            RevisorManager revisorManager = manager.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null){
                return true;
            }
        }

        if (player.hasPermission(config.getString("config.perms.msg-color"))) {
            message = PlayerStatic.setColor(msg);
        }

        MsgMethod msgMethod = manager.getPlayerMethods().getMsgMethod();
        Player targetplayer = target.getPlayer();

        msgMethod.sendPrivateMessage(player, targetplayer, message);

        String socialspyFormat = command.getString("commands.socialspy.spy")
                .replace("%player%", player.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", message);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));

        ReplyMethod reply = manager.getPlayerMethods().getReplyMethod();
        reply.setReply(playeruuid, targetuuid);
        return true;
    }
}
