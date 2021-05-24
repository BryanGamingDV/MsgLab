package me.bryangaming.chatlab.common.managers;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.common.managers.group.GroupEnum;
import me.bryangaming.chatlab.common.data.PartyData;
import me.bryangaming.chatlab.common.data.UserData;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.bryangaming.chatlab.common.utils.WorldData;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
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

        PlayerWrapper player = event.getPlayer();
        UserData playerData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        Set<PlayerWrapper> recipients = event.getRecipients();
        recipients.clear();

        if (playerData.getChannelType() == GroupEnum.PARTY) {
            PartyData partyData = pluginService.getServerData().getParty(playerData.getPartyID());

            List<PlayerWrapper> arrayList = new ArrayList<>();

            arrayList.add(Bukkit.getPlayer(partyData.getPlayerLeader()));
            partyData.getPlayers().forEach(uuid -> {
                arrayList.add(Bukkit.getPlayer(uuid));
            });

            addRecipients(event, arrayList);
            return;
        }

        if (playerData.getChannelType() == GroupEnum.CHANNEL) {
            for (PlayerWrapper onlinePlayer : ServerWrapper.getData().getOnlinePlayers()) {
                UserData userData = pluginService.getCache().getUserDatas().get(onlinePlayer.getUniqueId());

                if (userData.getChannelGroup().equalsIgnoreCase(playerData.getChannelGroup())) {
                    addRecipient(event, player);
                }
            }
            return;
        }

        if (utils.getBoolean("radial-chat.enabled")) {
            for (Entity entity : player.getNearbyEntities(utils.getInt("radial-chat.x"), utils.getInt("radial-chat.y"), utils.getInt("radial-chat.z"))) {

                if (entity instanceof PlayerWrapper) {
                    addRecipient(event, (PlayerWrapper) entity);
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
        }

        addRecipients(event, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    private void addRecipient(AsyncPlayerChatEvent event, PlayerWrapper recipient) {
        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();

        if (!ignoreManager.playerIsIgnored(event.getPlayer().getUniqueId(), recipient.getUniqueId())) {
            return;
        }

        event.getRecipients().add(recipient);
    }

    private void addRecipients(AsyncPlayerChatEvent event, Collection<PlayerWrapper> recipents) {
        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();

        recipents.forEach(recipient -> {
            if (!ignoreManager.playerIsIgnored(event.getPlayer().getUniqueId(), recipient.getUniqueId())) {
                event.getRecipients().add(recipient);
            }
        });
    }
}
