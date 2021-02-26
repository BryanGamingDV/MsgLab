package code.methods;

import code.PluginService;
import code.bukkitutils.RunnableManager;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.utils.Configuration;
import code.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class ListenerManaging {

    private final PluginService pluginService;

    public ListenerManaging(PluginService pluginService) {
        this.pluginService = pluginService;
        pluginService.getListManager().getModules().add("join_quit");
        pluginService.getListManager().getModules().add("motd");
    }

    public void setJoin(PlayerJoinEvent event) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!(utils.getBoolean("join.enabled"))) {
            return;
        }

        Player player = event.getPlayer();
        sendJoinMessage(event);

        if (pluginService.getPathManager().isOptionEnabled("motd")) {
            sendMotd(player);
        }

        sendCommands(player, "join");
    }

    public void sendJoinMessage(PlayerJoinEvent event){

        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();
        Player player = event.getPlayer();

        if (groupMethod.getJQGroup(player, "join").equalsIgnoreCase("silent")){
            event.setJoinMessage(null);
        }

        if (groupMethod.getJQGroup(player, "join").equalsIgnoreCase("none")) {
            return;
        }

        event.setJoinMessage(PlayerStatic.setFormat(player, groupMethod.getJQFormat(player, "join")
                    .replace("%player%", player.getName())));
    }

    public void sendCommands(Player player, String status) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean(status + ".commands.enabled")) {
            return;
        }

        StringFormat variable = pluginService.getStringFormat();
        RunnableManager runnableManager = pluginService.getManagingCenter().getRunnableManager();

        List<String> commands = utils.getStringList(status + ".motd.commands");
        commands.replaceAll(text -> variable.replacePlayerVariables(player, text));

        for (String command : commands) {
            runnableManager.sendCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void sendMotd(Player player) {

        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!utils.getBoolean("join.motd.enabled")) {
            return;
        }

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        StringFormat variable = pluginService.getStringFormat();

        List<String> motd = utils.getStringList("join.motd.format");
        motd.replaceAll(text -> variable.replacePlayerVariables(player, text));

        for (int i = 0; i < utils.getInt("join.motd.loop-blank"); i++) {
            playersender.sendMessage(player, "");
        }

        for (String motdPath : motd) {
            playersender.sendMessage(player, motdPath);
        }
    }

    public void setQuit(PlayerQuitEvent event) {
        Configuration utils = pluginService.getFiles().getBasicUtils();

        if (!(utils.getBoolean("quit.enabled"))) {
            return;
        }

        Player player = event.getPlayer();

        sendQuitMessage(event);
        sendCommands(player, "quit");
    }

    public void sendQuitMessage(PlayerQuitEvent event){

        GroupMethod groupMethod = pluginService.getPlayerMethods().getGroupMethod();
        Player player = event.getPlayer();

        if (groupMethod.getJQGroup(player, "quit").equalsIgnoreCase("silent")){
            event.setQuitMessage(null);
        }

        if (groupMethod.getJQGroup(player, "quit").equalsIgnoreCase("none")) {
            return;
        }

        event.setQuitMessage(PlayerStatic.setFormat(player, groupMethod.getJQFormat(player, "join")
                .replace("%player%", player.getName())));
    }
}
