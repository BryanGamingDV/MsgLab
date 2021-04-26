package me.bryangaming.chatlab.listeners.text;

import github.scarsz.discordsrv.util.DiscordUtil;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.ServerData;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.events.text.ChatEvent;
import me.bryangaming.chatlab.managers.HoverManager;
import me.bryangaming.chatlab.managers.RecipientManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.utils.Configuration;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener{

    private PluginService pluginService;

    public ChatListener(PluginService pluginService){
        this.pluginService = pluginService;
    }

    @EventHandler
    public void onChat(ChatEvent event){

        Player player = event.getSender();

        Configuration messages = pluginService.getFiles().getBasicUtils();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        ServerData serverData = pluginService.getServerData();
        CooldownData cooldownData = pluginService.getCooldownData();

        SenderManager playerMethod = pluginService.getPlayerManager().getSender();
        HoverManager hoverManager = pluginService.getPlayerManager().getHoverMethod();
        RecipientManager recipientManager = pluginService.getPlayerManager().getRecipientMethod();


        if (serverData.isMuted()) {
            if (!playerMethod.hasPermission(player, "chat.muted-bypass")) {
                playerMethod.sendMessage(player, messages.getString("error.chat.muted"));
                return;
            }
        }

        if (serverData.isWorldMuted(player.getWorld())) {
            if (!playerMethod.hasPermission(player, "chat.muted-bypass")) {
                playerMethod.sendMessage(player, messages.getString("error.chat.muted"));
                return;
            }
        }

        if (cooldownData.isTextSpamming(event.getSender().getUniqueId())) {
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

        for (Player recipient : event.getChatEvent().getRecipients()) {
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
                DiscordUtil.sendMessage(DiscordUtil.getTextChannelById(utils.getString("format.discord-srv.channel")), utils.getString("format.discord-srv.format")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
        }
    }

}
