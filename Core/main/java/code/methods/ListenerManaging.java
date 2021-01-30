package code.methods;

import code.PluginService;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.utils.Configuration;
import code.utils.StringFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class ListenerManaging {

    private final PluginService pluginService;

    public ListenerManaging(PluginService pluginService){
        this.pluginService = pluginService;
        pluginService.getListManager().getModules().add("join_quit");
        pluginService.getListManager().getModules().add("motd");
    }

    public void setJoin(PlayerJoinEvent event){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!(utils.getBoolean("utils.join.enabled"))){
            return;
        }

        Player player = event.getPlayer();

        event.setJoinMessage(PlayerStatic.setFormat(player, utils.getString("utils.join.format")
                .replace("%player%", player.getName())));

        if (pluginService.getPathManager().isOptionEnabled("motd")){
            sendMotd(player);
        }
    }

    public void sendMotd(Player player){

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("utils.join.motd.enabled")){
            return;
        }

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        StringFormat variable = pluginService.getStringFormat();

        List<String> motd = utils.getStringList("utils.join.motd.format");
        motd.replaceAll(text -> variable.replacePlayerVariables(player, text));

        for (int i = 0; i < utils.getInt("utils.join.motd.loop-blank"); i++){
            playersender.sendMessage(player, "");
        }

        for (String motdPath : motd){
            playersender.sendMessage(player, motdPath);
        }
    }

    public void setQuit(PlayerQuitEvent event){
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!(utils.getBoolean("utils.quit.enabled"))){
            return;
        }
        Player player = event.getPlayer();

        event.setQuitMessage(PlayerStatic.setFormat(player, utils.getString("utils.quit.format")
                .replace("%player%", player.getName())));
    }

}
