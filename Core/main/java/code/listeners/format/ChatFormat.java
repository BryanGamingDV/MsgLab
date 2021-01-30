package code.listeners.format;

import code.Manager;
import code.bukkitutils.WorldManager;
import code.cache.UserData;
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
import code.utils.addons.VaultSupport;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
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

    private final Manager manager;


    public ChatFormat(Manager manager) {
        this.manager = manager;
        manager.getListManager().getModules().add("chat_format");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        Configuration config = manager.getFiles().getConfig();
        Configuration command = manager.getFiles().getCommand();
        Configuration utils = manager.getFiles().getBasicUtils();

        PlayerMessage playersender = manager.getPlayerMethods().getSender();
        UserData playerStatus = manager.getCache().getPlayerUUID().get(player.getUniqueId());

        StaffChatMethod staffChatMethod = manager.getPlayerMethods().getStaffChatMethod();
        IgnoreMethod ignoreMethod = manager.getPlayerMethods().getIgnoreMethod();

        event.setCancelled(true);


        if (playerStatus.isClickMode()) {
            return;
        }

        if (manager.getPathManager().isCommandEnabled("staffchat")) {
            ChatMethod chatMethod = manager.getPlayerMethods().getChatMethod();

            if (staffChatMethod.isUsingStaffSymbol(event)) {
                staffChatMethod.getStaffSymbol(event);
                return;
            }

            if (playerStatus.isStaffchatMode()) {

                if (playerStatus.isClickMode()) {
                    chatMethod.unset(player.getUniqueId());
                }

                for (Player playeronline : Bukkit.getServer().getOnlinePlayers()) {
                    UserData onlineCache = manager.getCache().getPlayerUUID().get(playeronline.getUniqueId());

                    if (onlineCache.isStaffchatMode()) {
                        playersender.sendMessage(playeronline.getPlayer(), command.getString("commands.staff-chat.message")
                                .replace("%player%", player.getName())
                                .replace("%message%", event.getMessage()));
                    }
                    return;
                }
                return;
            }
        }

        if (!(manager.getPathManager().isOptionEnabled("chat_format"))) {
            return;

        }
        if (!(utils.getBoolean("utils.chat.enabled"))) {
            return;
        }

        RevisorManager revisorManager = manager.getRevisorManager();

        if (revisorManager.getAntiRepeatRevisor().isTextSpamming(player.getUniqueId())){
            return;
        }

        String message = event.getMessage();

        if (utils.getBoolean("utils.chat.allow-revisor")){
            message = revisorManager.revisor(event.getPlayer().getUniqueId(), event.getMessage());
        }

        GroupMethod groupChannel = manager.getPlayerMethods().getGroupMethod();

        String utilspath = groupChannel.getPlayerFormat(player);

        if (player.hasPermission(config.getString("config.perms.chat-color"))) {
            message = PlayerStatic.setColor(utilspath
                    .replace("%player%", player.getName())
                    .replace("%message%", message));

        } else {
            message = PlayerStatic.setColor(utilspath
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

        if (utils.getBoolean("utils.chat.per-world-chat.enabled")) {
            event.getRecipients().clear();

            if (utils.getBoolean("utils.chat.per-world-chat.all-worlds")) {
                for (String worldname : WorldManager.getAllWorldChat()) {
                    if (player.getWorld().getName().equalsIgnoreCase(worldname)) {
                        World world = Bukkit.getWorld(worldname);
                        playerList = new ArrayList<>(world.getPlayers());

                    }
                }

            } else {
                playerList = new ArrayList<>();
                for (String worldname : WorldManager.getWorldChat(player)){
                    World world = Bukkit.getWorld(worldname);
                    playerList.addAll(world.getPlayers());
                }
            }
        }else{
            playerList = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        }

        if (playerList == null){
            manager.getPlugin().getLogger().info("How you came here?" +
                    utils.getBoolean("utils.chat.per-world-chat.enabled"));
            return;
        }

        RadialChatMethod radialChatMethod = manager.getPlayerMethods().getRadialChatMethod();

        Iterator<Player> playerIterator = playerList.iterator();
        while (playerIterator.hasNext()){
           Player playerChannel = playerIterator.next();
           UserData playerCache = manager.getCache().getPlayerUUID().get(playerChannel.getUniqueId());

           if (!playerCache.equalsChannelGroup(playerStatus.getChannelGroup())) {
               playerIterator.remove();
           }

           if (ignoreMethod.playerIsIgnored(playerChannel.getUniqueId(), player.getUniqueId())){
               playerIterator.remove();

           }
        }

        if (utils.getBoolean("utils.chat.radial-chat.enabled")) {

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
        RevisorManager revisorManager = manager.getRevisorManager();
        Configuration utils = manager.getFiles().getBasicUtils();

        if (!manager.getPathManager().isCommandEnabled(event.getMessage())){
            manager.getPathManager().sendDisableMessage(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
            return;
        }

        for (String disabledCmds : utils.getStringList("utils.chat.cooldown.cmd.disabled-cmds")){
            if (("/" + disabledCmds).equalsIgnoreCase(event.getMessage())) {
                return;
            }
        }

        if (revisorManager.getAntiRepeatRevisor().isCmdSpamming(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }
}
