package code.methods.click;

import code.PluginService;
import code.bukkitutils.RunnableManager;
import code.bukkitutils.WorldManager;
import code.data.UserData;
import code.debug.ErrorManager;
import code.methods.player.PlayerMessage;
import code.methods.player.PlayerStatic;
import code.utils.Configuration;
import code.utils.HoverManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatMethod {

    private PluginService pluginService;
    private Boolean worldtype;

    public ChatMethod(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void setAgain(UUID uuid) {
        setWorld(uuid);
    }

    public void activateChat(UUID uuid, Boolean world) {

        worldtype = world;
        setWorld(uuid);
    }

    private void setWorld(UUID uuid){

        UserData userData = pluginService.getCache().getPlayerUUID().get(uuid);

        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();
        RunnableManager runnableManager = pluginService.getManagingCenter().getRunnableManager();

        Player player = Bukkit.getPlayer(uuid);

        Configuration command = pluginService.getFiles().getCommand();
        Configuration messages = pluginService.getFiles().getMessages();

        List<String> chatClick = userData.getClickChat();

        if (chatClick.size() < 1) {
            userData.toggleClickMode(true);
            playersender.sendMessage(player, command.getString("commands.broadcast.mode.load"));
            playersender.sendMessage(player, command.getString("commands.broadcast.mode.select.message"));
            playersender.sendMessage(player, "&ePut &6\"-cancel\" &eto cancel the clickchat mode.");
            return;
        }

        if (chatClick.size() < 2){
            runnableManager.waitSecond(player, 1,
                    command.getString("commands.broadcast.mode.select.command"),
                    "&aYou dont need to put '/' in this case." );
            return;
        }

        if (chatClick.size() < 3){
            runnableManager.waitSecond(player, 1,
                    command.getString("commands.broadcast.mode.select.cooldown"),
                    "&aIf you want now, use &8[&f-now&8]&a." );
            return;
        }


        if (chatClick.size() == 3){

            if (!ErrorManager.isNumber(chatClick.get(2))){
                playersender.sendMessage(player, messages.getString("error.chat.unknown-number")
                        .replace("%number%", chatClick.get(2)));
                userData.toggleClickMode(true);
                chatClick.remove(2);
                return;
            }

            int cooldown = Integer.parseInt(chatClick.get(2));

            runnableManager.waitSecond(player, 1, command.getString("commands.broadcast.mode.hover"));

            waitHover(player, cooldown);

        }
    }



    public void waitHover(Player player, int second){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pluginService.getPlugin(), new Runnable() {
            @Override
            public void run() {

                Configuration command = pluginService.getFiles().getCommand();

                UserData userData = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());
                List<String> chatClick = userData.getClickChat();

                HoverManager hover;
                if (worldtype){
                    hover = new HoverManager(PlayerStatic.setColor(command.getString("commands.broadcast.click_cmd.world")
                            .replace("%message%", chatClick.get(0))
                            .replace("%player%", player.getName())
                            .replace("%world%", player.getWorld().getName())));
                }else{
                    hover = new HoverManager(PlayerStatic.setColor(command.getString("commands.broadcast.click_cmd.global")
                            .replace("%message%", chatClick.get(0))
                            .replace("%player%", player.getName())));
                }

                hover.setHover(command.getString("commands.broadcast.click_cmd.format"), "/" + chatClick.get(1), true);

                Configuration utils = pluginService.getFiles().getBasicUtils();

                if (worldtype) {
                    List<Player> worldList;

                    if (utils.getBoolean("utils.chat.per-world-chat.all-worlds")){
                        worldList = getWorldChat(player);
                    }else{
                        worldList = getWorldList(player);
                    }

                    for (Player playeronline : worldList) {
                        playeronline.spigot().sendMessage(hover.getTextComponent());
                    }
                }else{
                    for (Player playeronline : Bukkit.getServer().getOnlinePlayers()){
                        playeronline.spigot().sendMessage(hover.getTextComponent());
                    }
                }

                userData.toggleClickMode(false);
                chatClick.clear();
            }
        }, 20L * second);
    }

    public List<Player> getWorldList(Player player){
        List<Player> listplayer = new ArrayList<>();
        for (String worldname : WorldManager.getWorldChat(player)) {
            World world = Bukkit.getWorld(worldname);
            listplayer.addAll(world.getPlayers());
        }
        return listplayer;
    }

    public List<Player> getWorldChat(Player player){
        for (String worldname : WorldManager.getAllWorldChat()) {
            if (player.getWorld().getName().equalsIgnoreCase(worldname)) {
                World world = Bukkit.getWorld(worldname);
                return world.getPlayers();

            }
        }
        return null;
    }

    public void unset(UUID uuid) {

        Player player = Bukkit.getPlayer(uuid);

        UserData userData = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());
        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

        Configuration command = pluginService.getFiles().getCommand();

        playersender.sendMessage(player, command.getString("commands.broadcast.mode.disabled"));
        userData.toggleClickMode(false);
        userData.getClickChat().clear();
    }
}
