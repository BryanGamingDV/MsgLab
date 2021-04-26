package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.commands.SocialSpyManager;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@Command(names = {"socialspy", "spy"})
public class SocialSpyCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager playerMethod;
    private final SocialSpyManager socialSpyManager;

    private final Configuration command;
    private final Configuration messages;

    public SocialSpyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.playerMethod = pluginService.getPlayerManager().getSender();
        this.socialSpyManager = pluginService.getPlayerManager().getSocialSpyMethod();

        this.command = pluginService.getFiles().getCommand();
        this.messages = pluginService.getFiles().getMessages();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player player, @OptArg("") String args) {
        if (args.isEmpty()) {
            socialSpyManager.toggleOption(player.getUniqueId());
            playerMethod.sendMessage(player.getPlayer(), command.getString("commands.socialspy.player.toggle")
                    .replace("%mode%", socialSpyManager.getStatus()));
            playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy");
            return true;
        }

        playerMethod.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("socialspy", "on, off, list", "<player>")));
        playerMethod.sendSound(player, SoundEnum.ERROR);

        return true;
    }

    @Command(names = "list")
    public boolean onListSubCommand(@Sender Player player) {
        List<String> socialspyList = new ArrayList<>();

        for (UserData cache : pluginService.getCache().getUserDatas().values()) {
            if (cache.isSocialSpyMode()) {
                socialspyList.add(cache.getPlayer().getName());
            }
        }

        if (socialspyList.isEmpty()) {
            playerMethod.sendMessage(player, messages.getString("error.socialspy.anybody"));
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(player, command.getString("commands.socialspy.list-spyplayers"));

        for (String playerSyping : socialspyList) {
            playerMethod.sendMessage(player, "&8- &f" + playerSyping);
        }

        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy list");
        return true;
    }


    @Command(names = "on")
    public boolean onOnSubCommand(@Sender Player player, @OptArg("") Player target) {

        UserData playerSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (target.getName().isEmpty()) {
            if (playerSpy.isSocialSpyMode()) {
                playerMethod.sendMessage(player, messages.getString("error.socialspy.activated"));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            playerSpy.toggleSocialSpy(true);
            playerMethod.sendMessage(player, command.getString("commands.socialspy.player.enabled"));
            playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy on");
        } else {

            if (!target.isOnline()) {
                playerMethod.sendMessage(player, messages.getString("error.player-offline"));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            UserData targetSpy = pluginService.getCache().getUserDatas().get(target.getUniqueId());
            String targetname = target.getName();

            if (targetSpy.isSocialSpyMode()) {
                playerMethod.sendMessage(player.getPlayer(), messages.getString("error.socialspy.arg-2-activated")
                        .replace("%arg-2%", targetname));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            targetSpy.toggleSocialSpy(true);
            playerMethod.sendMessage(player.getPlayer(), command.getString("commands.socialspy.arg-2.enabled")
                    .replace("%arg-2%", target.getName()));
            playerMethod.sendMessage(target.getPlayer(), command.getString("commands.socialspy.player.enabled"));
        }
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy on");
        return true;
    }

    @Command(names = "off")
    public boolean onOffSubCommand(@Sender Player player, @OptArg("") Player target) {

        UserData playerSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (target.getName().isEmpty()) {
            if (!(playerSpy.isSocialSpyMode())) {
                playerMethod.sendMessage(player, messages.getString("error.socialspy.unactivated"));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            playerSpy.toggleSocialSpy(false);
            playerMethod.sendMessage(player, command.getString("commands.socialspy.player.disabled"));
        } else {

            if (!target.isOnline()) {
                playerMethod.sendMessage(player, messages.getString("error.player-offline"));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            UserData targetSpy = pluginService.getCache().getUserDatas().get(target.getUniqueId());
            String targetname = target.getName();

            if (!(targetSpy.isSocialSpyMode())) {
                playerMethod.sendMessage(player, messages.getString("error.socialspy.arg-2-unactivated")
                        .replace("%arg-2%", targetname));
                playerMethod.sendSound(player, SoundEnum.ERROR);
                return true;
            }

            targetSpy.toggleSocialSpy(false);
            playerMethod.sendMessage(player, command.getString("commands.socialspy.arg-2.disabled")
                    .replace("%arg-2%", targetname));
            playerMethod.sendMessage(target, command.getString("commands.socialspy.player.disabled"));
        }

        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "socialspy off");
        return true;
    }

}
