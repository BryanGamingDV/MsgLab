package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.PartyData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.managers.group.GroupEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RecipientManager {

    private PluginService pluginService;

    public RecipientManager(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setRecipients(AsyncPlayerChatEvent event) {

        Configuration utils = pluginService.getFiles().getFormatsFile();

        Player player = event.getPlayer();
        UserData playerData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        Set<Player> recipients = event.getRecipients();
        recipients.clear();

        if (playerData.getChannelType() == GroupEnum.PARTY) {
            PartyData partyData = pluginService.getServerData().getParty(playerData.getPartyID());

            List<Player> arrayList = new ArrayList<>();

            arrayList.add(Bukkit.getPlayer(partyData.getPlayerLeader()));
            partyData.getPlayers().forEach(uuid -> {
                arrayList.add(Bukkit.getPlayer(uuid));
            });

            addRecipients(event, arrayList);
            return;
        }

        if (playerData.getChannelType() == GroupEnum.CHANNEL) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                UserData userData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

                if (userData.getChannelGroup().equalsIgnoreCase(playerData.getChannelGroup())) {
                    addRecipient(event, player);
                }
            }
            return;
        }

        if (utils.getBoolean("radial-chat.enabled")) {
            for (Entity entity : player.getNearbyEntities(utils.getInt("radial-chat.x"), utils.getInt("radial-chat.y"), utils.getInt("radial-chat.z"))) {

                if (entity instanceof Player) {
                    addRecipient(event, (Player) entity);
                }
            }
            return;
        }


        if (utils.getBoolean("per-world-chat.enabled")) {
            if (utils.getBoolean("per-world-chat.all-worlds")) {
                addRecipients(event, new ArrayList<>(player.getWorld().getPlayers()));
            } else {
                for (String worldName : WorldData.getWorldChat(player)) {
                    World world = Bukkit.getWorld(worldName);
                    addRecipients(event, new ArrayList<>(world.getPlayers()));
                }
            }
            return;
        } else {
            addRecipients(event, new ArrayList<>(Bukkit.getOnlinePlayers()));
        }

        return;
    }

    private void addRecipient(AsyncPlayerChatEvent event, Player recipient) {
        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();

        if (ignoreManager.playerIsIgnored(event.getPlayer().getUniqueId(), recipient.getUniqueId())) {
            event.getRecipients().add(recipient);
        }
    }

    private void addRecipients(AsyncPlayerChatEvent event, Collection<Player> recipents) {
        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();

        recipents.forEach(recipient -> {
            if (!ignoreManager.playerIsIgnored(event.getPlayer().getUniqueId(), recipient.getUniqueId())) {
                event.getRecipients().add(recipient);
            }
        });
    }
}
