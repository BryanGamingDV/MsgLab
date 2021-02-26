package code.listeners.format;

import code.PluginService;
import code.data.UserData;
import code.methods.HoverMethod;
import code.methods.RecipientMethod;
import code.methods.click.ClickChatMethod;
import code.methods.commands.StaffChatMethod;
import code.methods.player.PlayerMessage;
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

        UserData playerStatus = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

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

                    UserData onlineCache = pluginService.getCache().getPlayerUUID().get(playeronline.getUniqueId());

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

        RevisorManager revisorManager = pluginService.getRevisorManager();

        if (revisorManager.getAntiRepeatRevisor().isTextSpamming(player.getUniqueId())) {
            return;
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

        String commandRevisor = revisorManager.revisorCMD(event.getPlayer().getUniqueId(), event.getMessage().replace("/", "").split(" ")[0].toLowerCase());

        if (commandRevisor == null){
            event.setCancelled(true);
            return;
        }

        if (!pluginService.getPathManager().isPluginCommand(command)) {
            return;
        }

        if (!pluginService.getPathManager().isCommandEnabled(command)) {

            if (!pluginService.getPathManager().IsCommandEnabledInMc(command)) {
                return;
            }

            pluginService.getPathManager().sendDisableMessage(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
            return;
        }

        if (!playerMethod.hasPermission(event.getPlayer(), "commands." + command + ".main")) {
            playerMethod.sendMessage(event.getPlayer(), messages.getString("error.no-perms"));
            event.setCancelled(true);
        }

    }
}
