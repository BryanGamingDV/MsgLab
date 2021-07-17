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
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.utils.Configuration;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener{

    private final PluginService pluginService;

    public ChatListener(PluginService pluginService){
        this.pluginService = pluginService;
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChat(ChatEvent event){

        Player player = event.getSender();

        Configuration messagesFile = pluginService.getFiles().getMessagesFile();
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        ServerData serverData = pluginService.getServerData();
        CooldownData cooldownData = pluginService.getCooldownData();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        HoverManager hoverManager = pluginService.getPlayerManager().getHoverManager();
        RecipientManager recipientManager = pluginService.getPlayerManager().getRecipientManager();


        if (serverData.isMuted()) {
            if (!senderManager.hasPermission(player, "chat", "muted-bypass")) {
                senderManager.sendMessage(player, messagesFile.getString("chat.error.muted"));
                return;
            }
        }

        if (serverData.isWorldMuted(player.getWorld())) {
            if (!senderManager.hasPermission(player, "chat", "muted-bypass")) {
                senderManager.sendMessage(player, messagesFile.getString("chat.error.muted"));
                return;
            }
        }

        if (event.getUserData().getChannelType() == GroupEnum.CHANNEL){
            if (serverData.isChannelMuted(event.getUserData().getChannelGroup())){
                senderManager.sendMessage(player, messagesFile.getString("channel.error.muted"));
                return;
            }
        }

        if (cooldownData.isTextSpamming(event.getSender().getUniqueId())) {
            return;
        }


        String message = event.getMessage();

        if (formatsFile.getBoolean("chat-format.allow-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(player, message, TextRevisorEnum.TEXT);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                return;
            }

            message = textRevisorEvent.getMessageRevised();
        }


        recipientManager.setRecipients(event.getChatEvent());
        Component baseComponent;

        if (pluginService.getFiles().getConfigFile().getBoolean("options.allow-relational-placeholders")) {
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                for (Player recipient : event.getChatEvent().getRecipients()) {
                    baseComponent = hoverManager.convertBaseComponent(player, PlaceholderAPI.setRelationalPlaceholders(player, recipient, message));

                    BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();
                    bukkitAudiences.player(recipient).sendMessage(baseComponent);
                }
            }else{
                baseComponent = hoverManager.convertBaseComponent(player, message);

                for (Player recipient : event.getChatEvent().getRecipients()) {
                    BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();
                    bukkitAudiences.player(recipient).sendMessage(baseComponent);
                }
            }
        }else{
            baseComponent = hoverManager.convertBaseComponent(player, message);

            for (Player recipient : event.getChatEvent().getRecipients()) {
                BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();
                bukkitAudiences.player(recipient).sendMessage(baseComponent);
            }
        }

        if (formatsFile.getBoolean("chat-format.log.enabled")){
            System.out.println(formatsFile.getString("chat-format.log.format")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        }

        if (formatsFile.getBoolean("chat-format.discord-srv.enabled")){
            if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")){
                DiscordUtil.sendMessage(DiscordUtil.getTextChannelById(formatsFile.getString("chat-format.discord-srv.messages.from-mc")),
                        formatsFile.getString("chat-format.discord-srv.format")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
        }
    }

}
