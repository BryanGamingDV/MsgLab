package me.bryangaming.chatlab.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DiscordSrvListener {

    private final PluginService pluginService;
    private final Configuration formatsFile;

    public DiscordSrvListener(PluginService pluginService){
        this.pluginService = pluginService;
        this.formatsFile = pluginService.getFiles().getFormatsFile();
    }

    @Subscribe
    public void onDiscordChat(DiscordGuildMessagePreProcessEvent event){
        if (event.getChannel() != DiscordUtil.getTextChannelById(formatsFile.getString("format.discord-srv.channelID"))){
            return;
        }

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            if (!senderManager.hasUtilsPermission(player, formatsFile.getString("format.discord-srv.permission"))){
                continue;
            }

            senderManager.sendMessage(player, formatsFile.getString("format.discord-srv.message.from-discord")
                    .replace("%player%", event.getAuthor().getName())
                    .replace("%message%", event.getMessage().getContentStripped()));
        }

    }
}
