package me.bryangaming.chatlab.revisor.tabcomplete;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class TabFilter {

    private final PluginService pluginService;

    public TabFilter(PluginService pluginService) {
        this.pluginService = pluginService;
        setup();
    }

    public void setup() {
        Logger logger = pluginService.getPlugin().getLogger();

        if (!TextUtils.isAllowedHooked("ProtocolLib")){
            logger.info("The hook was disabled in config.");
            return;
        }

        Configuration filtersFile = pluginService.getFiles().getFiltersFile();
        ProtocolManager protocolManager = pluginService.getSupportManager().getProtocolSupport().getManager();

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

                if (!filtersFile.getBoolean("commands.tab-module.block.enabled")) {
                    return;
                }

                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                String command = packet.getSpecificModifier(String.class).read(0).toLowerCase();

                if (filtersFile.getBoolean("commands.tab-module.block.empty")) {
                    if (!command.contains(" ")) {
                        event.setCancelled(true);
                        sendMessage(player);
                        return;
                    }
                }

                if (!command.contains(" ")) {
                    return;
                }

                for (String commands : filtersFile.getStringList("commands.tab-module.block.commands")) {

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

        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration filtersFile = pluginService.getFiles().getFiltersFile();

        if (!filtersFile.getBoolean("message.tab-module.message.enabled")) {
            return;
        }

        senderManager.sendMessage(player, filtersFile.getString("message.tab-module.message.format"));


    }
}
