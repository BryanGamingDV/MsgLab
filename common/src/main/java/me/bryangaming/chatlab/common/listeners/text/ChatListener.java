package me.bryangaming.chatlab.common.listeners.text;

import github.scarsz.discordsrv.util.DiscordUtil;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.group.GroupEnum;
import me.bryangaming.chatlab.common.data.ServerData;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.common.events.text.ChatEvent;
import me.bryangaming.chatlab.common.managers.HoverManager;
import me.bryangaming.chatlab.common.managers.RecipientManager;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.revisor.CooldownData;
import me.bryangaming.chatlab.common.utils.Configuration;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener{

    private PluginService pluginService;

    public ChatListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onChat(ChatEvent event){

        PlayerWrapper player = event.getSender();

        Configuration messages = pluginService.getFiles().getMessagesFile();
        Configuration utils = pluginService.getFiles().getFormatsFile();

        ServerData serverData = pluginService.getServerData();
        CooldownData cooldownData = pluginService.getCooldownData();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        HoverManager hoverManager = pluginService.getPlayerManager().getHoverMethod();
        RecipientManager recipientManager = pluginService.getPlayerManager().getRecipientMethod();


        if (serverData.isMuted()) {
            if (!senderManager.hasPermission(player, "chat.muted-bypass")) {
                senderManager.sendMessage(player, messages.getString("error.chat.muted"));
                return;
            }
        }

        if (serverData.isWorldMuted(player.getWorld())) {
            if (!senderManager.hasPermission(player, "chat.muted-bypass")) {
                senderManager.sendMessage(player, messages.getString("error.chat.muted"));
                return;
            }
        }

        if (event.getUserData().getChannelType() == GroupEnum.CHANNEL){
            if (serverData.isChannelMuted(event.getUserData().getChannelGroup())){
                senderManager.sendMessage(player, messages.getString("error.channel.muted"));
                return;
            }
        }

        if (cooldownData.isTextSpamming(event.getSender().getUniqueId())) {
            senderManager.sendMessage(player, messages.getString("error.chat.muted"));
            return;
        }


        String message = event.getMessage();

        if (utils.getBoolean("options.allow-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(player, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                return;
            }

            message = textRevisorEvent.getMessageRevised();
        }

        recipientManager.setRecipients(event.getChatEvent());
        Component baseComponent = hoverManager.convertBaseComponent(player, message);

        for (PlayerWrapper recipient : event.getChatEvent().getRecipients()) {
            BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();
            bukkitAudiences.player(recipient).sendMessage(baseComponent);
        }

        if (utils.getBoolean("format.log.enabled")){
            System.out.println(utils.getString("format.log.format")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        }

        if (utils.getBoolean("format.discord-srv.enabled")){
            if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")){
                DiscordUtil.sendMessage(DiscordUtil.getTextChannelById(utils.getString("format.discord-srv.messages.from-mc")), utils.getString("format.discord-srv.format")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
        }
    }

}
