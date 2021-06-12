package me.bryangaming.chatlab.redis;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.ReplyManager;
import me.bryangaming.chatlab.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber extends JedisPubSub {

    private final PluginService pluginService;

    private final ReplyManager replyManager;
    private final StaffChatManager staffChatManager;
    private final SenderManager senderManager;

    public RedisSubscriber(PluginService pluginService){
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();

        this.replyManager = pluginService.getPlayerManager().getReplyManager();
        this.staffChatManager = pluginService.getPlayerManager().getStaffChatManager();
    }


    @Override
    public void onMessage(String channel, String messageData) {
        String messageType = messageData.split(";")[0];

        Player target;

        String sender;
        String message;

        switch (MessageType.valueOf(messageType)) {
            case SENDMESSAGE:
                target = Bukkit.getPlayer(messageData.split(";")[1]);

                message =  messageData.split(";")[2]
                        .replace("//,", ";");

                senderManager.sendMessage(target, message);
            case MSG:

                 target = Bukkit.getPlayer(messageData.split(";")[1]);

                 message =  messageData.split(";")[2]
                    .replace("//,", ";");


                senderManager.sendMessage(target, message);
                senderManager.playSound(target, SoundEnum.RECEIVE_MSG);
                break;

            case STAFFCHAT:
                 sender = messageData.split(";")[1];
                 message =  messageData.split(";")[2]
                        .replace("//,", ";");

                staffChatManager.sendToStaffPlayers(sender, message);
                break;
            case REPLY:
                target = Bukkit.getPlayer(messageData.split(";")[1]);
                sender =  messageData.split(";")[2]
                        .replace("//,", ";");

                replyManager.setBungeeReply(target.getUniqueId(), sender);
                break;
            case STREAM:
                message =  messageData.split(";")[1]
                        .replace("//,", ";");

                for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {
                    senderManager.sendMessage(playerOnline, message);
                    senderManager.playSound(playerOnline, SoundEnum.RECEIVE_STREAM);
                }
            case HELPOP:
                message =  messageData.split(";")[1]
                        .replace("//,", ";");

                for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {

                    UserData userData = pluginService.getCache().getUserDatas().get(playerOnline.getUniqueId());

                    if (!senderManager.hasPermission(playerOnline, "helpop", "watch") || !userData.isPlayerHelpOp()) {
                        return;
                    }

                    senderManager.sendMessage(playerOnline, message);
                    senderManager.playSound(playerOnline, SoundEnum.RECEIVE_HELPOP);
                }
            case BROADCAST:
                message =  messageData.split(";")[1]
                        .replace("//,", ";");

                for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {

                    senderManager.sendMessage(playerOnline, message);
                    senderManager.playSound(playerOnline, SoundEnum.RECEIVE_BROADCAST);
                }
        }

    }
}
