package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
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


@Command(names = {"socialspy", "sspy"})
public class SocialSpyCommand implements CommandClass {

    private final PluginService pluginService;

    private final SenderManager senderManager;
    private final Configuration messagesFile;

    public SocialSpyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.messagesFile = pluginService.getFiles().getMessagesFile();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @Command(names = {""})
    public boolean onMainCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                .replace("%usage%", TextUtils.getUsage("socialspy", "on, off, list", "<player>")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "list")
    public boolean onListSubCommand(@Sender Player sender) {
        List<String> socialspyList = new ArrayList<>();

        for (UserData cache : pluginService.getCache().getUserDatas().values()) {
            if (cache.isSocialSpyMode()) {
                socialspyList.add(cache.getPlayer().getName());
            }
        }

        if (socialspyList.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("socialspy.error.anybody"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, messagesFile.getString("socialspy.list-spyplayers"));

        for (String playerSyping : socialspyList) {
            senderManager.sendMessage(sender, "&8- &f" + playerSyping);
        }

        senderManager.playSound(sender, SoundEnum.ARGUMENT, "socialspy list");
        return true;
    }


    @Command(names = {"on"})
    public boolean onOnSubCommand(@Sender Player sender, @OptArg OfflinePlayer target) {

        UserData senderData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (target == null){
            if (senderData.isSocialSpyMode()) {
                senderManager.sendMessage(sender, messagesFile.getString("socialspy.error.activated"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }
            senderData.toggleSocialSpy(true);
            senderManager.sendMessage(sender, messagesFile.getString("socialspy.player.enabled"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "socialspy on");
        } else {

            if (!target.isOnline()) {
                senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            UserData targetData = pluginService.getCache().getUserDatas().get(target.getUniqueId());
            String targetName = target.getName();

            if (targetData.isSocialSpyMode()) {
                senderManager.sendMessage(sender.getPlayer(), messagesFile.getString("socialspy.error.arg-2-activated")
                        .replace("%arg-2%", targetName));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            targetData.toggleSocialSpy(true);
            senderManager.sendMessage(sender, messagesFile.getString("socialspy.arg-2.enabled")
                    .replace("%arg-2%", targetName));
            senderManager.sendMessage(target.getPlayer(), messagesFile.getString("socialspy.player.enabled"));
        }
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "socialspy on");
        return true;
    }

    @Command(names = {"off"})
    public boolean onOffSubCommand(@Sender Player player, @OptArg OfflinePlayer target) {

        UserData playerSpy = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (target == null) {
            if (!(playerSpy.isSocialSpyMode())) {
                senderManager.sendMessage(player, messagesFile.getString("socialspy.error.unactivated"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            playerSpy.toggleSocialSpy(false);
            senderManager.sendMessage(player, messagesFile.getString("socialspy.player.disabled"));
        } else {

            if (!target.isOnline()) {
                senderManager.sendMessage(player, messagesFile.getString("global-errors.player-offline"));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            UserData targetData = pluginService.getCache().getUserDatas().get(target.getUniqueId());
            String targetName = target.getName();

            if (!(targetData.isSocialSpyMode())) {
                senderManager.sendMessage(player, messagesFile.getString("socialspy.error.arg-2-unactivated")
                        .replace("%arg-2%", targetName));
                senderManager.playSound(player, SoundEnum.ERROR);
                return true;
            }

            targetData.toggleSocialSpy(false);
            senderManager.sendMessage(player, messagesFile.getString("socialspy.arg-2.disabled")
                    .replace("%arg-2%", targetName));
            senderManager.sendMessage(target, messagesFile.getString("socialspy.player.disabled"));
        }

        senderManager.playSound(player, SoundEnum.ARGUMENT, "socialspy off");
        return true;
    }

}
