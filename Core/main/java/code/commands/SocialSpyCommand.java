package code.commands;

import code.PluginService;
import code.bukkitutils.SoundCreator;
import code.data.UserData;
import code.methods.commands.SocialSpyMethod;
import code.methods.player.PlayerMessage;
import code.registry.ConfigManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class SocialSpyCommand implements CommandClass {

    private final PluginService pluginService;

    public SocialSpyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

    }

    @Command(names = {"socialspy", "spy"})
    public boolean onCommand(@Sender Player player, @OptArg String args, @OptArg OfflinePlayer target) {

        SocialSpyMethod socialSpyMethod = pluginService.getPlayerMethods().getSocialSpyMethod();

        ConfigManager files = pluginService.getFiles();
        PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

        SoundCreator sound = pluginService.getManagingCenter().getSoundManager();
        ModuleCheck moduleCheck = pluginService.getPathManager();

        Configuration messages = files.getMessages();
        Configuration command = files.getCommand();

        if (args == null) {
            socialSpyMethod.toggleOption(player.getUniqueId());
            playersender.sendMessage(player.getPlayer(), command.getString("commands.socialspy.player.toggle")
                    .replace("%mode%", socialSpyMethod.getStatus()));
            sound.setSound(player.getUniqueId(), "sounds.on-socialspy.toggle");
            return true;
        }

        UserData playerSpy = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (args.equalsIgnoreCase("list")) {

            List<String> socialspyList = new ArrayList<>();

            for (UserData cache : pluginService.getCache().getPlayerUUID().values()) {
                if (cache.isSocialSpyMode()) {
                    socialspyList.add(cache.getPlayer().getName());
                }
            }

            if (socialspyList.isEmpty()) {
                playersender.sendMessage(player, messages.getString("error.socialspy.anybody"));
                sound.setSound(player.getUniqueId(), "sounds.error");
                return true;
            }

            playersender.sendMessage(player, command.getString("commands.socialspy.list-spyplayers"));

            for (String playerSyping : socialspyList) {
                playersender.sendMessage(player, "&8- &f" + playerSyping);
            }

            sound.setSound(player.getUniqueId(), "sounds.on-socialspy.list");
            return true;
        }

        if (args.equalsIgnoreCase("on")) {
            if (target == null) {
                if (playerSpy.isSocialSpyMode()) {
                    playersender.sendMessage(player.getPlayer(), messages.getString("error.socialspy.activated"));
                    sound.setSound(player.getUniqueId(), "sounds.error");
                    return true;
                }

                playerSpy.toggleSocialSpy(true);
                playersender.sendMessage(player.getPlayer(), command.getString("commands.socialspy.player.enabled"));
            } else {

                if (!target.isOnline()) {
                    playersender.sendMessage(player.getPlayer(), messages.getString("error.player-offline"));
                    sound.setSound(player.getUniqueId(), "sounds.error");
                    return true;
                }

                UserData targetSpy = pluginService.getCache().getPlayerUUID().get(target.getUniqueId());
                String targetname = target.getName();

                if (targetSpy.isSocialSpyMode()) {
                    playersender.sendMessage(player.getPlayer(), messages.getString("error.socialspy.arg-2-activated").replace("%arg-2%", targetname));
                    sound.setSound(player.getUniqueId(), "sounds.error");
                    return true;
                }

                targetSpy.toggleSocialSpy(true);
                playersender.sendMessage(player.getPlayer(), command.getString("commands.socialspy.arg-2.enabled").replace("%arg-2%", target.getName()));
                playersender.sendMessage(target.getPlayer(), command.getString("commands.socialspy.player.enabled"));
            }

            sound.setSound(player.getUniqueId(), "sounds.on-socialspy.enable");
            return true;
        }

        if (args.equalsIgnoreCase("off")) {
            if (target == null) {

                if (!(playerSpy.isSocialSpyMode())) {
                    playersender.sendMessage(player.getPlayer(), messages.getString("error.socialspy.unactivated"));
                    sound.setSound(player.getUniqueId(), "sounds.error");
                    return true;
                }

                playerSpy.toggleSocialSpy(false);
                playersender.sendMessage(player.getPlayer(), command.getString("commands.socialspy.player.disabled"));
                return true;
            }

            if (!target.isOnline()) {
                playersender.sendMessage(player.getPlayer(), messages.getString("error.player-offline"));
                sound.setSound(player.getUniqueId(), "sounds.error");
                return true;
            }

            String targetname = target.getName();
            UserData targetSpy = pluginService.getCache().getPlayerUUID().get(target.getUniqueId());

            if (!(targetSpy.isSocialSpyMode())) {
                playersender.sendMessage(player.getPlayer(), messages.getString("error.socialspy.arg-2-unactivated")
                        .replace("%arg-2%", targetname));
                sound.setSound(player.getUniqueId(), "sounds.error");
                return true;
            }

            targetSpy.toggleSocialSpy(false);

            playersender.sendMessage(player.getPlayer(), command.getString("commands.socialspy.arg-2.disabled")
                    .replace("%arg-2%", targetname));
            playersender.sendMessage(target.getPlayer(), command.getString("commands.socialspy.player.disabled"));
            sound.setSound(player.getUniqueId(), "sounds.on-socialspy.disable");
            return true;
        }

        playersender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("socialspy", "on, off, list", "<player>")));
        sound.setSound(player.getUniqueId(), "sounds.error");

        return true;
    }

}
