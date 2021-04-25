package me.bryangaming.chatlab.revisor.tabcomplete;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.player.PlayerMessage;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class TabFitler {

    private PluginService pluginService;

    public TabFitler(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        ProtocolManager protocolManager = pluginService.getSupportManager().getProtocolSupport().getManager();
        Logger logger = pluginService.getPlugin().getLogger();

        if (protocolManager == null) {
            logger.info("Error you don't have ProtocolLib installed");
            return;
        }

        protocolManager.addPacketListener(new PacketAdapter(pluginService.getPlugin(), ListenerPriority.HIGHEST, new PacketType[]{PacketType.Play.Client.TAB_COMPLETE}) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) {
                    return;
                }

                Player player = event.getPlayer();

                if (!utils.getBoolean("revisor-cmd.tab-module.block.enabled")) {
                    return;
                }

                PacketContainer packet = event.getPacket();
                String command = packet.getSpecificModifier(String.class).read(0).toLowerCase();

                if (utils.getBoolean("revisor-cmd.tab-module.block.empty")) {
                    if (!command.contains(" ")) {
                        event.setCancelled(true);
                        sendMessage(player);
                        return;
                    }
                }

                if (!command.contains(" ")) {
                    return;
                }

                for (String commands : utils.getStringList("revisor-cmd.tab-module.block.commands")) {

                    if (!command.split(" ")[0].startsWith(commands)) {
                        continue;
                    }

                    event.setCancelled(true);
                    sendMessage(player);
                    return;
                }

                event.setCancelled(false);

            }
        });
    }

    public void sendMessage(Player player) {

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("revisor.tab-module.message.enabled")) {
            return;
        }

        playerMethod.sendMessage(player, utils.getString("revisor.tab-module.message.format"));


    }
}
