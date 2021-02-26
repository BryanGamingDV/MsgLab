package code.revisor.tabcomplete;

import code.PluginService;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import code.utils.SupportManager;
import code.utils.addons.ProtocolSupport;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class BlockRevisor {

    private PluginService pluginService;

    public BlockRevisor(PluginService pluginService){
        this.pluginService = pluginService;
        setup();
    }

    public void setup(){

        Configuration utils = pluginService.getFiles().getBasicUtils();

        ProtocolManager protocolManager = pluginService.getSupportManager().getProtocolSupport().getManager();

        Logger logger = pluginService.getPlugin().getLogger();
        if (protocolManager == null){
            logger.info("Error you don't have ProtocolLib installed");
            return;
        }

        protocolManager.addPacketListener(new PacketAdapter(pluginService.getPlugin(), ListenerPriority.HIGHEST, new PacketType[]{PacketType.Play.Client.TAB_COMPLETE}){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE){
                    return;
                }

                Player player = event.getPlayer();

                PacketContainer packet = event.getPacket();
                String command = packet.getSpecificModifier(String.class).read(0).toLowerCase();

                if (utils.getBoolean("revisor.tab-module.block-empty")){
                    if (command.equalsIgnoreCase("/")){
                        event.setCancelled(true);
                        sendMessage(player);
                    }
                }

                for (String commands : utils.getStringList("revisor.tab-module.commands")){
                    if (command.equalsIgnoreCase(commands)){
                        event.setCancelled(true);
                        sendMessage(player);
                    }
                }

            }
        });
    }

    public void sendMessage(Player player){

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("revisor.tab-module.message.enabled")){
            return;
        }

        playerMethod.sendMessage(player, utils.getString("revisor.tab-module.message.format"));


    }
}
