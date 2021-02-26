package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.bukkitutils.gui.manager.GuiManager;
import code.data.UserData;
import code.events.SocialSpyEvent;
import code.methods.commands.MsgMethod;
import code.methods.commands.ReplyMethod;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.registry.ConfigManager;
import code.revisor.RevisorManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MsgCommand implements CommandClass {

    private final PluginService pluginService;

    public MsgCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = {"msg", "pm", "tell", "t", "w", "whisper"})

    public boolean onCommand(@Sender Player sender, @OptArg OfflinePlayer target, @OptArg("") @Text String msg) {

        ConfigManager files = pluginService.getFiles();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration command = files.getCommand();
        Configuration messages = files.getMessages();

        UUID playeruuid = sender.getUniqueId();

        if (target == null) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("msg", "<player>", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        UUID targetuuid = target.getUniqueId();

        if (target.getName().equalsIgnoreCase("-all")) {
            playerMethod.sendMessage(sender, "%c &fEmmm, use /broadcast or the chat.");
            playerMethod.sendMessage(sender, "&8- EasterEgg #3");
            return true;
        }

        if (target.getName().equalsIgnoreCase("-online")) {

            if (Bukkit.getServer().getOnlinePlayers().size() < 2) {
                playerMethod.sendMessage(sender, messages.getString("error.nobody-offline"));
                return true;
            }

            GuiManager guiManager = pluginService.getManagingCenter().getGuiManager();
            guiManager.openInventory(playeruuid, "online", 0);
            return true;
        }

        UserData playerMsgToggle = pluginService.getCache().getPlayerUUID().get(playeruuid);

        if (target.getName().equalsIgnoreCase("-toggle")) {

            if (!(playerMethod.hasPermission(sender, "commands.msg.toggle"))) {
                playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            if (msg.isEmpty()) {
                if (!(playerMsgToggle.isSocialSpyMode())) {
                    playerMsgToggle.toggleSocialSpy(true);
                    playerMethod.sendMessage(sender, command.getString("commands.msg-toggle.player.activated"));
                    return true;
                }

                playerMsgToggle.toggleSocialSpy(false);
                playerMethod.sendMessage(sender, command.getString("commands.msg-toggle.player.unactivated"));
                return true;
            }

            Player you = Bukkit.getPlayer(msg);

            if (you == null) {
                playerMethod.sendMessage(sender, messages.getString("error.player-offline"));
                sound.setSound(playeruuid, "sounds.error");
                return true;
            }

            UserData targetMsgToggle = pluginService.getCache().getPlayerUUID().get(you.getUniqueId());

            if (!(targetMsgToggle.isMsgtoggleMode())) {
                targetMsgToggle.toggleMsg(true);
                playerMethod.sendMessage(sender, command.getString("commands.msg-toggle.arg-1.activated")
                        .replace("%arg-1%", you.getName()));
                playerMethod.sendMessage(you, command.getString("commands.msg-toggle.player.activated"));

            } else {
                targetMsgToggle.toggleMsg(false);
                playerMethod.sendMessage(sender, command.getString("commands.msg-toggle.arg-1.unactivated")
                        .replace("%arg-1%", you.getName()));
                playerMethod.sendMessage(you, command.getString("commands.msg-toggle.player.unactivated"));
            }
            sound.setSound(sender.getUniqueId(), "sounds.on-togglepm");
            return true;
        }


        if (!(target.isOnline())) {
            playerMethod.sendMessage(sender, messages.getString("error.player-offline"));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            playerMethod.sendMessage(sender, messages.getString("error.same-player")
                    .replace("%player%", sender.getName()));
            sound.setSound(playeruuid, "sounds.error");
            return true;

        }

        UserData targetToggled = pluginService.getCache().getPlayerUUID().get(targetuuid);

        if (targetToggled == null) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("msg", "<player>", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        if (targetToggled.isMsgtoggleMode()) {
            playerMethod.sendMessage(sender, command.getString("commands.msg-toggle.msg")
                    .replace("%player%", target.getName()));
            return true;
        }


        if (msg.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("msg", "<player>", "<message>")));
            sound.setSound(playeruuid, "sounds.error");
            return true;
        }

        String message = String.join(" ", msg);

        if (command.getBoolean("commands.msg-reply.enable-revisor")) {
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(playeruuid, message);

            if (message == null) {
                return true;
            }
        }

        if (!playerMethod.hasPermission(sender, "color.commands")) {
            message = "<pre>" + message + "</pre>";
        }

        MsgMethod msgMethod = pluginService.getPlayerMethods().getMsgMethod();
        Player targetplayer = target.getPlayer();

        msgMethod.sendPrivateMessage(sender, targetplayer, message);

        String socialspyFormat = command.getString("commands.socialspy.spy")
                .replace("%player%", sender.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", message);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));

        ReplyMethod reply = pluginService.getPlayerMethods().getReplyMethod();
        reply.setReply(playeruuid, targetuuid);
        return true;
    }
}
