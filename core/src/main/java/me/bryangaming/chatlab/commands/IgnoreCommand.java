package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand implements CommandClass {

    private final PluginService pluginService;

    public IgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = "ignore")
    public boolean onIgnoreCommand(@Sender Player sender, @OptArg OfflinePlayer target) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = pluginService.getFiles().getPlayersFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        UUID playeruuid = sender.getUniqueId();

        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", TextUtils.getUsage("ignore", "<sender>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Map<UUID, List<String>> ignoredList = pluginService.getCache().getIgnorelist();
        List<String> playerIgnoredList = playersFile.getStringList("players." + playeruuid + ".players-ignored");

        if (target.getName().equalsIgnoreCase("-list")) {

            if (!ignoredList.containsKey(playeruuid) || playerIgnoredList.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("ignore.error.anybody"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            senderManager.sendMessage(sender, messagesFile.getString("ignore.list-ignoredplayers"));

            for (String playersIgnored : playerIgnoredList) {
                senderManager.sendMessage(sender, "&8- &a" + playersIgnored);
            }

            senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!(senderManager.isOnline(sender))) {
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (senderManager.isVanished(sender)){
            senderManager.sendMessage(sender, messagesFile.getString("global-errors.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            senderManager.sendMessage(sender, messagesFile.getString("ignore.error.ignore-yourself"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getPlayer().getName();
        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();

        if (!ignoredList.containsKey(playeruuid)) {
            ignoreManager.ignorePlayer(sender, target.getUniqueId());
            senderManager.sendMessage(sender, messagesFile.getString("ignore.player-ignored")
                    .replace("%player%", targetname));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!(playerIgnoredList.contains(targetname))) {
            ignoreManager.ignorePlayer(sender, target.getUniqueId());
            senderManager.sendMessage(sender, messagesFile.getString("ignore.player-ignored")
                    .replace("%player%", targetname));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        ignoreManager.unignorePlayer(sender, target.getUniqueId());
        senderManager.sendMessage(sender, messagesFile.getString("ignore.player-unignored")
                .replace("%player%", targetname));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
        return true;


    }


}
