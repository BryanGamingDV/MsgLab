package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.events.CommandSpyEvent;
import me.bryangaming.chatlab.events.revisor.TextRevisorEnum;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.events.text.ChatEvent;
import me.bryangaming.chatlab.loader.command.CommandsType;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.click.ClickChatManager;
import me.bryangaming.chatlab.managers.commands.StaffChatManager;
import me.bryangaming.chatlab.revisor.CooldownData;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
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
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        Configuration messagesFile = pluginService.getFiles().getMessagesFile();
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();

        UserData playerStatus = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        StaffChatManager staffChatManagerManager = pluginService.getPlayerManager().getStaffChatManager();

        if (playerStatus.isClickMode()) {
            return;
        }

        if (pluginService.getFiles().getConfigFile().getBoolean("modules.staff-chat.enabled")) {
            ClickChatManager clickChatManager = pluginService.getPlayerManager().getChatManagent();

            if (staffChatManagerManager.isUsingStaffSymbol(event)) {
                event.setCancelled(true);
                staffChatManagerManager.getStaffSymbol(event);
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

                    senderManager.sendMessage(playeronline.getPlayer(), messagesFile.getString("staff-chat.format")
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));
                });
                return;
            }
        }

        if (formatsFile.getBoolean("chat-format.enabled")) {
            event.setCancelled(true);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.getServer().getPluginManager().callEvent(new ChatEvent(player, playerStatus, event, event.getMessage()));
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

        Configuration messagesFile = pluginService.getFiles().getMessagesFile();
        Configuration formatsFile = pluginService.getFiles().getFormatsFile();
        Configuration filterFile = pluginService.getFiles().getFiltersFile();

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        String commandText = event.getMessage().replace("/", "").split(" ")[0].toLowerCase();

        if (commandText.isEmpty()){
            return;
        }

        if (cooldownData.isCmdSpamming(event.getPlayer().getUniqueId())) {
            if (!filterFile.getStringList("cooldown.cmd.disabled-cmds").contains(commandText)){
                event.setCancelled(true);
                return;
            }
        }

        Bukkit.getPluginManager().callEvent(new CommandSpyEvent(event.getPlayer().getName(), event.getMessage().substring(TextUtils.countRepeatedCharacters(event.getMessage(), '/')).toLowerCase()));

        if (formatsFile.getBoolean("chat-format.allow-revisor")) {
            TextRevisorEvent textRevisorEvent = new TextRevisorEvent(event.getPlayer(), commandText, TextRevisorEnum.COMMAND);
            Bukkit.getServer().getPluginManager().callEvent(textRevisorEvent);

            if (textRevisorEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }

        CommandsType commandsType;
        try{
            commandsType = CommandsType.valueOf(commandText.toUpperCase());
        }catch (IllegalArgumentException exception) {
            return;
        }

        if (!senderManager.hasPermission(event.getPlayer(), commandsType.getCommandName(), commandsType.getSuffix() + ".main")) {
            senderManager.sendMessage(event.getPlayer(), messagesFile.getString("global-errors.no-perms"));
            event.setCancelled(true);
        }

    }
}
