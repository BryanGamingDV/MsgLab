package me.bryangaming.chatlab.managers.click;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.WorldData;
import me.bryangaming.chatlab.utils.string.TextUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClickChatManager {

    private final PluginService pluginService;

    private final SenderManager senderManager;
    private final Configuration messagesFile;

    private ClickType clickType;

    public ClickChatManager(PluginService pluginService) {
        this.pluginService = pluginService;
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    public void setAgain(UUID uuid) {
        setWorld(uuid);
    }

    public void activateChat(UUID uuid, ClickType clickType) {

        this.clickType = clickType;
        setWorld(uuid);
    }

    private void setWorld(UUID uuid) {

        UserData userData = pluginService.getCache().getUserDatas().get(uuid);

        Player player = Bukkit.getPlayer(uuid);
        List<String> chatClick = userData.getClickChat();

        if (chatClick.size() < 1) {
            userData.toggleClickMode(true);
            senderManager.sendMessage(player, messagesFile.getString("broadcast.mode.load"));
            senderManager.sendMessage(player, messagesFile.getString("broadcast.mode.select.message"));
            senderManager.sendMessage(player, "&eUse &6\"-cancel\" &eto cancel the clickchat mode.");
            return;
        }

        if (chatClick.size() < 2) {
            senderManager.sendMessagesLater(player, 1,
                    messagesFile.getString("broadcast.mode.select.command"),
                    "&aYou don't need to use '/' in this case.");
            return;
        }

        if (chatClick.size() < 3) {
            senderManager.sendMessagesLater(player, 1,
                    messagesFile.getString("broadcast.mode.select.cooldown"),
                    "&aIf you want to broadcast now, use &8[&f-now&8]&a.");
            return;
        }


        if (chatClick.size() == 3) {

            if (!TextUtils.isNumber(chatClick.get(2))) {
                senderManager.sendMessage(player, messagesFile.getString("error.click-chat.unknown-number")
                        .replace("%number%", chatClick.get(2)));
                userData.toggleClickMode(true);
                chatClick.remove(2);
                return;
            }

            int cooldown = Integer.parseInt(chatClick.get(2));

            senderManager.sendMessageLater(player, 1, messagesFile.getString("broadcast.mode.hover"));

            broadcastMessage(player, cooldown);

        }
    }


    public void broadcastMessage(Player sender, int second) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
            @Override
            public void run() {

                MiniMessage miniMessage = MiniMessage.get();
                BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();

                UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());
                List<String> chatClick = userData.getClickChat();

                Configuration formatsFile = pluginService.getFiles().getFormatsFile();
                String clickedMessage = messagesFile.getString("broadcast.click_cmd." + clickType.getPath())
                        .replace("%message%", chatClick.get(0));

                List<Player> onlinePlayers;

                if (clickType == ClickType.WORLD) {
                    if (formatsFile.getBoolean("per-world-chat.all-worlds")) {
                        onlinePlayers = sender.getWorld().getPlayers();
                    } else {
                        onlinePlayers = getWorldList(sender);
                    }

                    clickedMessage = clickedMessage.replace("%world%", sender.getWorld().getName());
                }else{
                    onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                }

                Component component = TextUtils.convertTextToComponent(sender, clickedMessage);

                component = component.hoverEvent(HoverEvent.showText(miniMessage.parse(messagesFile.getString("broadcast.click_cmd.format"))));
                component = component.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + chatClick.get(1)));

                for (Player player : onlinePlayers){
                    bukkitAudiences.player(player).sendMessage(component);
                }

                userData.toggleClickMode(false);
                chatClick.clear();
            }
        }, 20L * second);
    }

    public List<Player> getWorldList(Player player) {
        List<Player> playerList = new ArrayList<>();

        for (String worldName : WorldData.getWorldChat(player)) {
            World world = Bukkit.getWorld(worldName);
            playerList.addAll(world.getPlayers());
        }
        return playerList;
    }



    public void unset(UUID playerUniqueId) {

        UserData userData = pluginService.getCache().getUserDatas().get(playerUniqueId);

        senderManager.sendMessage(Bukkit.getPlayer(playerUniqueId), messagesFile.getString("broadcast.mode.disabled"));

        userData.toggleClickMode(false);
        userData.getClickChat().clear();
    }
}
