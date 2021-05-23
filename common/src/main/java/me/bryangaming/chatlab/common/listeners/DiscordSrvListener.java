package me.bryangaming.chatlab.common.listeners;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

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

        for (PlayerWrapper player : Bukkit.getServer().getOnlinePlayers()){
            if (!senderManager.hasUtilsPermission(player, formatsFile.getString("format.discord-srv.permission"))){
                continue;
            }

            senderManager.sendMessage(player, formatsFile.getString("format.discord-srv.message.from-discord")
                    .replace("%player%", event.getAuthor().getName())
                    .replace("%message%", event.getMessage().getContentStripped()));
        }

    }
}
