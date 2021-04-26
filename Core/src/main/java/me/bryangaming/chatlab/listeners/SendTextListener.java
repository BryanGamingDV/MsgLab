package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.CommandSpyEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.events.text.ChatEvent;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class SendTextListener implements Listener {

    private final PluginService pluginService;


    public SendTextListener(PluginService pluginService) {
        this.pluginService = pluginService;
        pluginService.getListManager().getModules().add("chat_format");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        Configuration command = pluginService.getFiles().getCommand();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        UserData playerStatus = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        StaffChatManager staffChatMethod = pluginService.getPlayerMethods().getStaffChatMethod();

        if (playerStatus.isClickMode()) {
            return;
        }

        if (pluginService.getListManager().isEnabledOption("commands", "staffchat")) {
            ClickChatManager clickChatManager = pluginService.getPlayerMethods().getChatManagent();

            if (staffChatMethod.isUsingStaffSymbol(event)) {
                event.setCancelled(true);
                staffChatMethod.getStaffSymbol(event);
                return;
            }

            if (playerStatus.isStaffchatMode()) {
                event.setCancelled(true);

                if (playerStatus.isClickMode()) {
                    clickChatManager.unset(player.getUniqueId());
                }

                Bukkit.getServer().getOnlinePlayers().forEach(playeronline -> {

                    UserData onlineCache = pluginService.getCache().getUserDatas().get(playeronline.getUniqueId());

                    if (!onlineCache.isStaffchatMode()) {
                        return;
                    }

                    playerMethod.sendMessage(playeronline.getPlayer(), command.getString("commands.staff-chat.message")
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));
                });
                return;
            }
        }

        if (pluginService.getListManager().isEnabledOption("modules","chat_format") && utils.getBoolean("options.enabled") && utils.getBoolean("format.enabled")) {
            event.setCancelled(true);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(new ChatEvent(player, event, event.getMessage()));
                }
            });

        }
    }


    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        StringFormat stringFormat = pluginService.getStringFormat();

        String command = event.getCommand().substring(stringFormat.countRepeatedCharacters(event.getCommand(), '/')).toLowerCase();
        Bukkit.getPluginManager().callEvent(new CommandSpyEvent("Console", command));

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        CooldownData cooldownData = pluginService.getCooldownData();

        Configuration messages = pluginService.getFiles().getMessages();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        StringFormat stringFormat = pluginService.getStringFormat();

        if (cooldownData.isCmdSpamming(event.getPlayer().getUniqueId())) {
            if (!pluginService.getPathManager().isCommandDisabledInCooldown(event.getMessage())) {
                event.setCancelled(true);
            }
            return;
        }

        Bukkit.getPluginManager().callEvent(new CommandSpyEvent(event.getPlayer().getName(), event.getMessage().substring(stringFormat.countRepeatedCharacters(event.getMessage(), '/')).toLowerCase()));

        String commandText = event.getMessage().replace("/", "").split(" ")[0].toLowerCase();

        if (utils.getBoolean("options.allow-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(event.getPlayer(), commandText, TextRevisorEnum.COMMAND);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!pluginService.getPathManager().isPluginCommand(commandText)) {
            return;
        }

        if (!playerMethod.hasPermission(event.getPlayer(), "commands." + commandText + ".main")) {
            playerMethod.sendMessage(event.getPlayer(), messages.getString("error.no-perms"));
            event.setCancelled(true);
        }

    }
}
