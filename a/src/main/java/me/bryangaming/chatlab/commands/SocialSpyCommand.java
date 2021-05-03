package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.SocialSpyManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@Command(names = {"socialspy", "spy"})
public class SocialSpyCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;
    private final SocialSpyManager socialSpyManager;

    private final Configuration commandFile;
    private final Configuration messagesFile;

    public SocialSpyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.senderManager = pluginService.getPlayerManager().getSender();
        this.socialSpyManager = pluginService.getPlayerManager().getSocialSpyMethod();

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Command(names = "")
    public boolean onMainCommand(@Sender Player player, @OptArg("") String args) {

        if (args.isEmpty()) {
            socialSpyManager.toggleOption(player.getUniqueId());
            senderManager.sendMessage(player.getPlayer(), commandFile.getString("commands.socialspy.player.toggle")
                    .replace("%mode%", socialSpyManager.getStatus()));
            senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy");
            return true;
        }

        senderManager.sendMessage(player, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("socialspy", "on, off, list", "<player>")));
        senderManager.playSound(player, SoundEnum.ERROR);
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
            senderManager.sendMessage(player, messagesFile.getString("error.socialspy.anybody"));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(player, commandFile.getString("commands.socialspy.list-spyplayers"));

        for (String playerSyping : socialspyList) {
            senderManager.sendMessage(player, "&8- &f" + playerSyping);
        }

        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy list");
        return true;
    }


    @Command(names = "on")
    public boolean onOnSubCommand(@Sender Player player, OfflinePlayer target) {

        UserData playerSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (target == null){
            if (playerSpy.isSocialSpyMode()) {
                senderManager.sendMessage(player, messagesFile.getString("error.socialspy.activated"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            playerSpy.toggleSocialSpy(true);
            senderManager.sendMessage(player, commandFile.getString("commands.socialspy.player.enabled"));
            senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy on");
        } else {

            if (!target.isOnline()) {
                senderManager.sendMessage(player, messagesFile.getString("error.player-offline"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            UserData targetSpy = pluginService.getCache().getUserDatas().get(target.getUniqueId());
            String targetname = target.getName();

            if (targetSpy.isSocialSpyMode()) {
                senderManager.sendMessage(player.getPlayer(), messagesFile.getString("error.socialspy.arg-2-activated")
                        .replace("%arg-2%", targetname));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            targetSpy.toggleSocialSpy(true);
            senderManager.sendMessage(player, commandFile.getString("commands.socialspy.arg-2.enabled")
                    .replace("%arg-2%", target.getName()));
            senderManager.sendMessage(target.getPlayer(), commandFile.getString("commands.socialspy.player.enabled"));
        }
        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy on");
        return true;
    }

    @Command(names = "off")
    public boolean onOffSubCommand(@Sender Player player, @OptArg("") Player target) {

        UserData playerSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (target.getName().isEmpty()) {
            if (!(playerSpy.isSocialSpyMode())) {
                senderManager.sendMessage(player, messagesFile.getString("error.socialspy.unactivated"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            playerSpy.toggleSocialSpy(false);
            senderManager.sendMessage(player, commandFile.getString("commands.socialspy.player.disabled"));
        } else {

            if (!target.isOnline()) {
                senderManager.sendMessage(player, messagesFile.getString("error.player-offline"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            UserData targetSpy = pluginService.getCache().getUserDatas().get(target.getUniqueId());
            String targetName = target.getName();

            if (!(targetSpy.isSocialSpyMode())) {
                senderManager.sendMessage(player, messagesFile.getString("error.socialspy.arg-2-unactivated")
                        .replace("%arg-2%", targetName));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            targetSpy.toggleSocialSpy(false);
            senderManager.sendMessage(player, commandFile.getString("commands.socialspy.arg-2.disabled")
                    .replace("%arg-2%", targetName));
            senderManager.sendMessage(target, commandFile.getString("commands.socialspy.player.disabled"));
        }

        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy off");
        return true;
    }

}
