package me.bryangaming.chatlab.common.managers.click;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.utils.WorldData;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.World;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClickChatManager {

    private PluginService pluginService;
    private Boolean worldtype;

    public ClickChatManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setAgain(UUID uuid) {
        setWorld(uuid);
    }

    public void activateChat(UUID uuid, Boolean world) {

        worldtype = world;
        setWorld(uuid);
    }

    private void setWorld(UUID uuid) {

        UserData userData = pluginService.getCache().getUserDatas().get(uuid);

        PlayerWrapper player = Bukkit.getPlayer(uuid);
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration command = pluginService.getFiles().getCommandFile();
        Configuration messages = pluginService.getFiles().getMessagesFile();

        List<String> chatClick = userData.getClickChat();

        if (chatClick.size() < 1) {
            userData.toggleClickMode(true);
            senderManager.sendMessage(player, command.getString("commands.broadcast.mode.load"));
            senderManager.sendMessage(player, command.getString("commands.broadcast.mode.select.message"));
            senderManager.sendMessage(player, "&eUse &6\"-cancel\" &eto cancel the clickchat mode.");
            return;
        }

        if (chatClick.size() < 2) {
            senderManager.sendMessagesLater(player, 1,
                    command.getString("commands.broadcast.mode.select.command"),
                    "&aYou don't need to use '/' in this case.");
            return;
        }

        if (chatClick.size() < 3) {
            senderManager.sendMessagesLater(player, 1,
                    command.getString("commands.broadcast.mode.select.cooldown"),
                    "&aIf you want to broadcast now, use &8[&f-now&8]&a.");
            return;
        }


        if (chatClick.size() == 3) {

            if (!TextUtils.isNumber(chatClick.get(2))) {
                senderManager.sendMessage(player, messages.getString("error.click-chat.unknown-number")
                        .replace("%number%", chatClick.get(2)));
                userData.toggleClickMode(true);
                chatClick.remove(2);
                return;
            }

            int cooldown = Integer.parseInt(chatClick.get(2));

            senderManager.sendMessageLater(player, 1, command.getString("commands.broadcast.mode.hover"));

            waitHover(player, cooldown);

        }
    }


    public void waitHover(PlayerWrapper sender, int second) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
            @Override
            public void run() {

                MiniMessage miniMessage = MiniMessage.get();

                BukkitAudiences bukkitAudiences = pluginService.getPlugin().getBukkitAudiences();

                Configuration command = pluginService.getFiles().getCommandFile();

                UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());
                List<String> chatClick = userData.getClickChat();

                Component component;
                if (worldtype) {
                    component = miniMessage.parse(command.getString("commands.broadcast.click_cmd.world")
                            .replace("%message%", chatClick.get(0))
                            .replace("%player%", sender.getName())
                            .replace("%world%", sender.getWorld().getName()));

                } else {
                    component = miniMessage.parse(command.getString("commands.broadcast.click_cmd.global")
                            .replace("%message%", chatClick.get(0))
                            .replace("%player%", sender.getName()));

                }
                component = component.hoverEvent(HoverEvent.showText(miniMessage.parse(command.getString("commands.broadcast.click_cmd.format"))));
                component = component.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + chatClick.get(1)));

                Configuration utils = pluginService.getFiles().getFormatsFile();

                if (worldtype) {
                    List<PlayerWrapper> worldList;

                    if (utils.getBoolean("chat.per-world-chat.all-worlds")) {
                        worldList = getWorldChat(sender);
                    } else {
                        worldList = getWorldList(sender);
                    }

                    for (PlayerWrapper playeronline : worldList) {
                        bukkitAudiences.player(playeronline).sendMessage(component);
                    }
                } else {
                    for (PlayerWrapper playeronline : Bukkit.getServer().getOnlinePlayers()) {
                        bukkitAudiences.player(playeronline).sendMessage(component);
                    }
                }

                userData.toggleClickMode(false);
                chatClick.clear();
            }
        }, 20L * second);
    }

    public List<PlayerWrapper> getWorldList(PlayerWrapper player) {
        List<PlayerWrapper> listplayer = new ArrayList<>();
        for (String worldname : WorldData.getWorldChat(player)) {
            World world = Bukkit.getWorld(worldname);
            listplayer.addAll(world.getPlayers());
        }
        return listplayer;
    }

    public List<PlayerWrapper> getWorldChat(PlayerWrapper player) {
        return player.getWorld().getPlayers();
    }

    public void unset(UUID uuid) {

        PlayerWrapper player = Bukkit.getPlayer(uuid);

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration command = pluginService.getFiles().getCommandFile();

        senderManager.sendMessage(player, command.getString("commands.broadcast.mode.disabled"));
        userData.toggleClickMode(false);
        userData.getClickChat().clear();
    }
}
