package code.listeners.format;

import code.PluginService;
import code.bukkitutils.WorldData;
import code.data.UserData;
import code.methods.GroupMethod;
import code.methods.chat.RadialChatMethod;
import code.methods.commands.IgnoreMethod;
import code.methods.commands.StaffChatMethod;
import code.methods.click.ChatMethod;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.revisor.RevisorManager;
import code.utils.Configuration;
import code.utils.HoverManager;
import code.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Iterator;
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

        Configuration config = pluginService.getFiles().getConfig();
        Configuration command = pluginService.getFiles().getCommand();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        UserData playerStatus = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        StaffChatMethod staffChatMethod = pluginService.getPlayerMethods().getStaffChatMethod();
        IgnoreMethod ignoreMethod = pluginService.getPlayerMethods().getIgnoreMethod();

        event.setCancelled(true);


        if (playerStatus.isClickMode()) {
            return;
        }

        if (pluginService.getPathManager().isCommandEnabled("staffchat")) {
            ChatMethod chatMethod = pluginService.getPlayerMethods().getChatMethod();

            if (staffChatMethod.isUsingStaffSymbol(event)) {
                staffChatMethod.getStaffSymbol(event);
                return;
            }

            if (playerStatus.isStaffchatMode()) {

                if (playerStatus.isClickMode()) {
                    chatMethod.unset(player.getUniqueId());
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

        if (revisorManager.getAntiRepeatRevisor().isTextSpamming(player.getUniqueId())){
            return;
        }

        String message = event.getMessage();

        if (utils.getBoolean("chat.allow-revisor")){
            message = revisorManager.revisor(event.getPlayer().getUniqueId(), event.getMessage());
        }

        GroupMethod groupChannel = pluginService.getPlayerMethods().getGroupMethod();

        String utilspath = groupChannel.getPlayerFormat(player);

        if (playerMethod.hasPermission(player, "color.chat")){
            message = PlayerStatic.setColor(utilspath
                    .replace("%world%", player.getWorld().getName())
                    .replace("%player%", player.getName())
                    .replace("%message%", message));

        } else {
            message = PlayerStatic.setColor(utilspath
                            .replace("%world%", player.getWorld().getName())
                            .replace("%player%", player.getName())
                    , message);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlayerStatic.setFormat(player, message);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")){
            message = StringFormat.replaceVault(player, message);
        }

        List<String> utilshover = groupChannel.getPlayerHover(player);

        HoverManager hover = new HoverManager(message);
        String hovertext = hover.hoverMessage(player, utilshover);

        String commandtext = groupChannel.getPlayerCmd(player)
                .replace("%player%", player.getName());
        hover.setHover(hovertext, commandtext);

        List<Player> playerList = null;

        if (utils.getBoolean("chat.per-world-chat.enabled")) {
            event.getRecipients().clear();

            if (utils.getBoolean("chat.per-world-chat.all-worlds")) {
                for (String worldname : WorldData.getAllWorldChat()) {
                    if (player.getWorld().getName().equalsIgnoreCase(worldname)) {
                        World world = Bukkit.getWorld(worldname);
                        playerList = new ArrayList<>(world.getPlayers());

                    }
                }

            } else {
                playerList = new ArrayList<>();
                for (String worldname : WorldData.getWorldChat(player)){
                    World world = Bukkit.getWorld(worldname);
                    playerList.addAll(world.getPlayers());
                }
            }
        }else{
            playerList = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        }

        if (playerList == null){
            pluginService.getPlugin().getLogger().info("How you came here?" +
                    utils.getBoolean("chat.per-world-chat.enabled"));
            return;
        }

        RadialChatMethod radialChatMethod = pluginService.getPlayerMethods().getRadialChatMethod();

        Iterator<Player> playerIterator = playerList.iterator();
        while (playerIterator.hasNext()){
           Player playerChannel = playerIterator.next();
           UserData playerCache = pluginService.getCache().getPlayerUUID().get(playerChannel.getUniqueId());

           if (!playerCache.equalsChannelGroup(playerStatus.getChannelGroup())) {
               playerIterator.remove();
           }

           if (ignoreMethod.playerIsIgnored(playerChannel.getUniqueId(), player.getUniqueId())){
               playerIterator.remove();

           }
        }

        if (utils.getBoolean("chat.radial-chat.enabled")) {

            Iterator<Player> playerRadialIterator = playerList.iterator();
            List<Player> radialPlayerList = radialChatMethod.getRadialPlayers(player);

            while (playerRadialIterator.hasNext()){
                Player playerRadial = playerRadialIterator.next();

                if (radialPlayerList.contains(playerRadial)){
                    continue;
                }

                radialPlayerList.remove(player);
            }
        }
        event.getRecipients().addAll(playerList);

        for (Player recipient : event.getRecipients()) {
            recipient.spigot().sendMessage(hover.getTextComponent());
        }
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        RevisorManager revisorManager = pluginService.getRevisorManager();

        Configuration config = pluginService.getFiles().getConfig();

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();


        if (revisorManager.getAntiRepeatRevisor().isCmdSpamming(event.getPlayer().getUniqueId())){
            if (!pluginService.getPathManager().isCommandDisabledInCooldown(event.getMessage())) {
                event.setCancelled(true);
            }
            return;
        }


        if (pluginService.getPathManager().isPluginCommand(event.getMessage())){
            return;
        }

        if (!pluginService.getPathManager().isCommandEnabled(event.getMessage())){
            pluginService.getPathManager().sendDisableMessage(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
            return;
        }

        if (playerMethod.hasPermission(event.getPlayer(), "commands." + event.getMessage())) {
            playerMethod.sendMessage(event.getPlayer(), config.getString("error.no-perms"));
            event.setCancelled(true);
        }

    }
}
