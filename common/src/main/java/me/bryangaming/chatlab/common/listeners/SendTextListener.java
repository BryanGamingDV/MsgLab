package me.bryangaming.chatlab.common.listeners;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.common.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.common.events.text.ChatEvent;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.click.ClickChatManager;
import me.bryangaming.chatlab.common.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.common.utils.module.ModuleType;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.events.CommandSpyEvent;
import me.bryangaming.chatlab.common.revisor.CooldownData;
import me.bryangaming.chatlab.common.utils.Configuration;

import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import org.bukkit.event.EventPriority;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class SendTextListener implements Listener {

    private final PluginService pluginService;


    public SendTextListener(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        PlayerWrapper player = event.getPlayer();

        Configuration commandFile = pluginService.getFiles().getCommandFile();
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        UserData playerStatus = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        StaffChatManager staffChatManagerMethod = pluginService.getPlayerManager().getStaffChatMethod();

        if (playerStatus.isClickMode()) {
            return;
        }

        if (pluginService.getListManager().isEnabledOption(ModuleType.COMMAND, "staffchat")) {
            ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();

            if (staffChatManagerMethod.isUsingStaffSymbol(event)) {
                event.setCancelled(true);
                staffChatManagerMethod.getStaffSymbol(event);
                return;
            }

            if (playerStatus.isStaffchatMode()) {
                event.setCancelled(true);

                if (playerStatus.isClickMode()) {
                    clickChatManager.unset(player.getUniqueId());
                }

                ServerWrapper.getData().getOnlinePlayers().forEach(playeronline -> {

                    UserData onlineCache = pluginService.getCache().getUserDatas().get(playeronline.getUniqueId());

                    if (!onlineCache.isStaffchatMode()) {
                        return;
                    }

                    senderManager.sendMessage(playeronline.getPlayer(), commandFile.getString("commands.staff-chat.message")
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));
                });
                return;
            }
        }

        if (pluginService.getListManager().isEnabledOption(ModuleType.MODULE,"chat_format") && formatsFile.getBoolean("options.enabled") && formatsFile.getBoolean("format.enabled")) {
            event.setCancelled(true);

            ServerWrapper.getData().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    ServerWrapper.getData().getPluginManager().callEvent(new ChatEvent(player, playerStatus, event, event.getMessage()));
                }
            });

        }
    }


    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().substring(TextUtils.countRepeatedCharacters(event.getCommand(), '/')).toLowerCase();
        Bukkit.getPluginManager().callEvent(new CommandSpyEvent("Console", command));

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        CooldownData cooldownData = pluginService.getCooldownData();

        Configuration messages = pluginService.getFiles().getMessagesFile();
        Configuration utils = pluginService.getFiles().getFormatsFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        String commandText = event.getMessage().replace("/", "").split(" ")[0].toLowerCase();

        if (cooldownData.isCmdSpamming(event.getPlayer().getUniqueId())) {
            if (!utils.getStringList("filters.cmd.disabled-cmds").contains(commandText)){
                event.setCancelled(true);
                return;
            }
        }

        Bukkit.getPluginManager().callEvent(new CommandSpyEvent(event.getPlayer().getName(), event.getMessage().substring(TextUtils.countRepeatedCharacters(event.getMessage(), '/')).toLowerCase()));

        if (utils.getBoolean("options.allow-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(event.getPlayer(), commandText, TextRevisorEnum.COMMAND);
            ServerWrapper.getData().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!pluginService.getListManager().isPluginCommand(commandText)) {
            return;
        }

        if (!senderManager.hasPermission(event.getPlayer(), "commands." + commandText + ".main")) {
            senderManager.sendMessage(event.getPlayer(), messages.getString("error.no-perms"));
            event.setCancelled(true);
        }

    }
}