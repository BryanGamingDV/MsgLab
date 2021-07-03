package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.SocialSpyEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.MsgManager;
import me.bryangaming.chatlab.managers.commands.ReplyManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
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

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration configFile = pluginService.getFiles().getConfigFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        UUID playerUniqueId = sender.getUniqueId();

        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("msg", "<player>", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        UUID targetUniqueId = target.getUniqueId();

        if (target.getName().equalsIgnoreCase("-all")) {
            senderManager.sendMessage(sender, "%c &fEmmm, use /broadcast or the chat.");
            senderManager.sendMessage(sender, "&8- EasterEgg #3");
            return true;
        }

        if (target.getName().equalsIgnoreCase("-online")) {

            if (Bukkit.getServer().getOnlinePlayers().size() < 2) {
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.error.nobody-offline"));
                return true;
            }

            senderManager.openInventory(sender, "online");
            return true;
        }

        UserData playerMsgToggle = pluginService.getCache().getUserDatas().get(playerUniqueId);

        if (target.getName().equalsIgnoreCase("-toggle")) {

            if (!(senderManager.hasPermission(sender, "msg-reply", "msg.toggle"))) {
                senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-perms"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            if (msg.isEmpty()) {
                if (!(playerMsgToggle.isSocialSpyMode())) {
                    playerMsgToggle.toggleSocialSpy(true);
                    senderManager.sendMessage(sender, messagesFile.getString("msg-reply.toggle.player.activated"));
                    return true;
                }

                playerMsgToggle.toggleSocialSpy(false);
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.toggle.player.unactivated"));
                return true;
            }

            Player you = Bukkit.getPlayer(msg);

            if (you == null) {
                senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            UserData targetMsgToggle = pluginService.getCache().getUserDatas().get(you.getUniqueId());

            if (!(targetMsgToggle.isMsgtoggleMode())) {
                targetMsgToggle.toggleMsg(true);
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.arg-1.activated")
                        .replace("%arg-1%", you.getName()));
                senderManager.sendMessage(you, messagesFile.getString("msg-reply.player.activated"));

            } else {
                targetMsgToggle.toggleMsg(false);
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.arg-1.unactivated")
                        .replace("%arg-1%", you.getName()));
                senderManager.sendMessage(you, messagesFile.getString("msg-reply.player.unactivated"));
            }
            senderManager.playSound(you, SoundEnum.ARGUMENT, "msg -toggle");
            return true;
        }

        if (!(senderManager.isOnline(target))) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (senderManager.isVanished(target.getPlayer())){
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            senderManager.sendMessage(sender, messagesFile.getString("msg-reply.error.same-player")
                    .replace("%player%", sender.getName()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;

        }

        UserData targetToggled = pluginService.getCache().getUserDatas().get(targetUniqueId);

        if (targetToggled == null) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("msg", "<player>", "<message>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (targetToggled.isMsgtoggleMode()) {
            senderManager.sendMessage(sender, messagesFile.getString("msg-reply.toggle.msg")
                    .replace("%player%", target.getName()));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }


        if (msg.isEmpty()) {

            ReplyManager reply = pluginService.getPlayerManager().getReplyManager();

            if (!playerMsgToggle.isMsgPlayerMode()){
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.format.talk-mode.enabled")
                        .replace("%target%", target.getName()));
                playerMsgToggle.setMsgChatMessage(true);
                reply.setReply(playerUniqueId, targetUniqueId);
            }else{
                senderManager.sendMessage(sender, messagesFile.getString("msg-reply.format.talk-mode.disabled")
                        .replace("%target%", target.getName()));
                playerMsgToggle.setMsgChatMessage(false);
                playerMsgToggle.setRepliedPlayer(null);
            }
            return true;
        }

        String message = String.join(" ", msg);

        if (configFile.getBoolean("msg-reply.enable-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(sender, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                return true;
            }

            message = textRevisorEvent.getMessageRevised();
        }

        MsgManager msgManager = pluginService.getPlayerManager().getMsgManager();
        Player targetPlayer = target.getPlayer();

        if (configFile.getBoolean("options.redis.enabled")){
            msgManager.sendBungeePrivateMessage(sender, target.getName(), message);
         }else {
            msgManager.sendPrivateMessage(sender, targetPlayer, message);
        }
        String socialspyFormat = messagesFile.getString("socialspy.format")
                .replace("%player%", sender.getName())
                .replace("%arg-1%", target.getName())
                .replace("%message%", message);

        Bukkit.getPluginManager().callEvent(new SocialSpyEvent(socialspyFormat));

        ReplyManager reply = pluginService.getPlayerManager().getReplyManager();
        reply.setReply(playerUniqueId, targetUniqueId);
        return true;
    }
}
