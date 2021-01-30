package code.methods.click;

import code.Manager;
import code.cache.UserData;
import code.methods.commands.StaffChatMethod;
import code.methods.player.PlayerMessage;
import code.utils.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;

public class ChatClickMethod implements Listener {

    private final Manager manager;

    public ChatClickMethod(Manager manager){
        this.manager = manager;

    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        
        UserData userData = manager.getCache().getPlayerUUID().get(uuid);
        PlayerMessage playersender = manager.getPlayerMethods().getSender();

        if (!(userData.isClickMode())){
            return;
        }

        List<String> clickchat = userData.getClickChat();
        ChatMethod chatMethod = manager.getPlayerMethods().getChatMethod();

        Configuration command = manager.getFiles().getCommand();

        if (event.getMessage().startsWith("-cancel")) {
            chatMethod.unset(uuid);
            return;
        }

        StaffChatMethod staffChatMethod = manager.getPlayerMethods().getStaffChatMethod();

        if (staffChatMethod.isUsingStaffSymbol(event)) {
            staffChatMethod.getStaffSymbol(event);
                return;
        }

        if (clickchat.size() < 1) {
            clickchat.add(event.getMessage());
            playersender.sendMessage(event.getPlayer(), command.getString("commands.broadcast.mode.selected.message")
                    .replace("%message%", clickchat.get(0)));
            chatMethod.setAgain(uuid);
            return;
        }

        if (clickchat.size() < 2){
            clickchat.add(event.getMessage());
            playersender.sendMessage(event.getPlayer(), command.getString("commands.broadcast.mode.selected.command")
                    .replace("%command%", clickchat.get(1)));
            chatMethod.setAgain(uuid);
            return;
        }

        if (clickchat.size() < 3){

            playersender.sendMessage(event.getPlayer(), command.getString("commands.broadcast.mode.selected.cooldown")
                    .replace("%cooldown%", clickchat.get(2)));

            if (event.getMessage().startsWith("-now")) {
                clickchat.add("1");
            } else {
                clickchat.add(event.getMessage());
            }

            userData.toggleClickMode(false);
            chatMethod.setAgain(uuid);
        }

    }
}
