package code.commands;

import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.managers.click.ClickChatMethod;
import code.managers.player.PlayerMessage;
import code.revisor.RevisorManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

@Command(names = {"broadcastworld", "bcw", "bcworld"})
public class BroadcastWorldCommand implements CommandClass {

    private final PluginService pluginService;

    private final ClickChatMethod clickChatMethod;
    private final PlayerMessage playerMethod;

    private final ModuleCheck moduleCheck;

    private final Configuration command;
    private final Configuration messages;

    public BroadcastWorldCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.clickChatMethod = pluginService.getPlayerMethods().getChatManagent();

        this.moduleCheck = pluginService.getPathManager();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onMainSubCommand(@Sender Player sender, @OptArg("") @Text String args) {

        if (args.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("broadcastworld", "<message>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        String message = String.join(" ", args);

        if (command.getBoolean("commands.broadcast.enable-revisor")) {
            RevisorManager revisorManager = pluginService.getRevisorManager();
            message = revisorManager.revisor(sender.getUniqueId(), message);

            if (message == null) {
                return true;
            }
        }

        for (Player onlinePlayer : clickChatMethod.getWorldChat(sender)) {
            playerMethod.sendMessage(onlinePlayer, command.getString("commands.broadcast.text.world")
                    .replace("%world%", sender.getWorld().getName())
                    .replace("%player%", sender.getName())
                    .replace("%message%", message));
            playerMethod.sendSound(sender, SoundEnum.RECEIVE_BROADCASTWORLD);
        }
        return true;
    }

    @Command(names = "-click")
    public boolean onClickSubCommand(@Sender Player sender) {

        if (!playerMethod.hasPermission(sender, "commands.broadcastworld.click")) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;
        }

        clickChatMethod.activateChat(sender.getUniqueId(), true);
        return true;

    }

}
