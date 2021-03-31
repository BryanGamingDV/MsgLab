package code.listeners.format;

import code.PluginService;
import code.data.ServerData;
import code.data.UserData;
import code.events.CommandSpyEvent;
import code.managers.HoverMethod;
import code.managers.RecipientMethod;
import code.managers.click.ClickChatMethod;
import code.managers.commands.StaffChatMethod;
import code.managers.player.PlayerMessage;
import code.revisor.RevisorManager;
import code.utils.Configuration;
import code.utils.StringFormat;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;

public class ChatFormat implements Listener {

    private final PluginService pluginService;


    public ChatFormat(PluginService pluginService) {
        this.pluginService = pluginService;
        pluginService.getListManager().getModules().add("chat_format");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        Configuration command = pluginService.getFiles().getCommand();
        Configuration utils = pluginService.getFiles().getBasicUtils();
        Configuration messages = pluginService.getFiles().getMessages();

        UserData playerStatus = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        StaffChatMethod staffChatMethod = pluginService.getPlayerMethods().getStaffChatMethod();
        HoverMethod hoverMethod = pluginService.getPlayerMethods().getHoverMethod();

        event.setCancelled(true);

        if (playerStatus.isClickMode()) {
            return;
        }

        if (pluginService.getPathManager().isCommandEnabled("staffchat")) {
            ClickChatMethod clickChatMethod = pluginService.getPlayerMethods().getChatManagent();

            if (staffChatMethod.isUsingStaffSymbol(event)) {
                staffChatMethod.getStaffSymbol(event);
                return;
            }

            if (playerStatus.isStaffchatMode()) {

                if (playerStatus.isClickMode()) {
                    clickChatMethod.unset(player.getUniqueId());
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

        if (!(pluginService.getPathManager().isOptionEnabled("chat_format"))) {
            return;

        }
        if (!(utils.getBoolean("chat.enabled"))) {
            return;
        }

        ServerData serverData = pluginService.getServerData();
        RevisorManager revisorManager = pluginService.getRevisorManager();

        if (revisorManager.getAntiRepeatRevisor().isTextSpamming(player.getUniqueId())) {
            return;
        }

        if (serverData.isMuted()) {
            if (!playerMethod.hasPermission(player, "chat.muted-bypass")) {
                playerMethod.sendMessage(player, messages.getString("error.chat.muted"));
                return;
            }
        }

        String message = event.getMessage();

        if (utils.getBoolean("chat.allow-revisor")) {
            message = revisorManager.revisor(event.getPlayer().getUniqueId(), event.getMessage());
        }

        RecipientMethod recipientMethod = pluginService.getPlayerMethods().getRecipientMethod();

        List<Player> playerList = recipientMethod.getRecipients(event);
        event.getRecipients().addAll(playerList);

        Component baseComponent = hoverMethod.convertBaseComponent(player, message);

        for (Player recipient : event.getRecipients()) {
            BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();

            bukkitAudiences.player(recipient).sendMessage(baseComponent);
        }
    }


    @EventHandler
    public void onServerCommand(ServerCommandEvent event){
        StringFormat stringFormat = pluginService.getStringFormat();

        String command = event.getCommand().substring(stringFormat.countRepeatedCharacters(event.getCommand(), '/')).toLowerCase();

        Bukkit.getPluginManager().callEvent(new CommandSpyEvent("Console", command));

    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        RevisorManager revisorManager = pluginService.getRevisorManager();

        Configuration messages = pluginService.getFiles().getMessages();
        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();

        if (revisorManager.getAntiRepeatRevisor().isCmdSpamming(event.getPlayer().getUniqueId())) {
            if (!pluginService.getPathManager().isCommandDisabledInCooldown(event.getMessage())) {
                event.setCancelled(true);
            }
            return;
        }

        StringFormat stringFormat = pluginService.getStringFormat();

        String command = event.getMessage().substring(stringFormat.countRepeatedCharacters(event.getMessage(), '/')).split(" ")[0].toLowerCase();


        Bukkit.getPluginManager().callEvent(new CommandSpyEvent(event.getPlayer().getName(), event.getMessage().substring(stringFormat.countRepeatedCharacters(event.getMessage(), '/')).toLowerCase()));

        String commandRevisor = revisorManager.revisorCMD(event.getPlayer().getUniqueId(), event.getMessage().replace("/", "").split(" ")[0].toLowerCase());

        if (commandRevisor == null) {
            event.setCancelled(true);
            return;
        }

        if (!pluginService.getPathManager().isPluginCommand(command)) {
            return;
        }

        if (!playerMethod.hasPermission(event.getPlayer(), "commands." + command + ".main")) {
            playerMethod.sendMessage(event.getPlayer(), messages.getString("error.no-perms"));
            event.setCancelled(true);
        }

    }
}
