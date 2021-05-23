package me.bryangaming.chatlab.common.commands;

import me.bryangaming.chatlab.common.PluginService;
import me.bryangaming.chatlab.common.managers.SenderManager;
import me.bryangaming.chatlab.common.managers.commands.IgnoreManager;
import me.bryangaming.chatlab.common.managers.sound.SoundEnum;
import me.bryangaming.chatlab.common.utils.string.TextUtils;
import me.bryangaming.chatlab.common.utils.Configuration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.bryangaming.chatlab.common.wrapper.annotation.SenderAnnotWrapper;
import org.bukkit.OfflinePlayer;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand implements CommandClass {

    private final PluginService pluginService;

    public IgnoreCommand(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Command(names = "ignore")
    public boolean onIgnoreCommand(@SenderAnnotWrapper  PlayerWrapper sender, @OptArg OfflinePlayer target) {

        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        Configuration playersFile = pluginService.getFiles().getPlayersFile();
        Configuration commandFile = pluginService.getFiles().getCommandFile();
        Configuration messagesFile = pluginService.getFiles().getMessagesFile();

        UUID playeruuid = sender.getUniqueId();

        if (target == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("ignore", "<sender>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Map<UUID, List<String>> ignoredList = pluginService.getCache().getIgnorelist();
        List<String> playerIgnoredList = playersFile.getStringList("players." + playeruuid + ".players-ignored");

        if (target.getName().equalsIgnoreCase("-list")) {

            if (!ignoredList.containsKey(playeruuid) || playerIgnoredList.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.ignore.anybody"));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            senderManager.sendMessage(sender, commandFile.getString("commands.ignore.list-ignoredplayers"));

            for (String playersIgnored : playerIgnoredList) {
                senderManager.sendMessage(sender, "&8- &a" + playersIgnored);
            }

            senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!target.isOnline()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(target)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            senderManager.sendMessage(sender, messagesFile.getString("error.ignore.ignore-yourself"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        String targetname = target.getPlayer().getName();
        IgnoreManager ignoreManager = pluginService.getPlayerManager().getIgnoreMethod();

        if (!ignoredList.containsKey(playeruuid)) {
            ignoreManager.ignorePlayer(sender, target.getUniqueId());
            senderManager.sendMessage(sender, commandFile.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        if (!(playerIgnoredList.contains(targetname))) {
            ignoreManager.ignorePlayer(sender, target.getUniqueId());
            senderManager.sendMessage(sender, commandFile.getString("commands.ignore.player-ignored")
                    .replace("%player%", targetname));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
            return true;
        }

        ignoreManager.unignorePlayer(sender, target.getUniqueId());
        senderManager.sendMessage(sender, commandFile.getString("commands.ignore.player-unignored")
                .replace("%player%", targetname));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "ignore");
        return true;


    }


}
