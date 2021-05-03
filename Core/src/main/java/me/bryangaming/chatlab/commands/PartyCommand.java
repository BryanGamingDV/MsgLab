package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.PartyData;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.commands.PartyManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Command(names = "party")
public class PartyCommand implements CommandClass {

    private PluginService pluginService;

    private final Configuration commandFile;
    private final Configuration messagesFile;

    private final PartyManager partyManager;
    private final SenderManager senderManager;

    public PartyCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.commandFile = pluginService.getFiles().getCommandFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.partyManager = pluginService.getPlayerManager().getPartyMethod();
        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean onMainSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, commandFile.getStringList("commands.party.help"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "party");
        return true;
    }

    @Command(names = "create")
    public boolean onCreateSubCommand(@Sender Player sender) {
        partyManager.createParty(sender);
        return true;
    }

    @Command(names = "join")
    public boolean onJoinSubCommand(@Sender Player sender, @OptArg("") String leader) {

        if (leader.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("party", "join", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player player = Bukkit.getPlayerExact(leader);

        if (player == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(player)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        partyManager.joinParty(sender, player);
        return true;
    }


    @Command(names = "leave")
    public boolean onQuitSubCommand(@Sender Player sender) {
        partyManager.leaveParty(sender);
        return true;
    }

    @Command(names = "set")
    public boolean onSetSubCommand(@Sender Player sender, @OptArg("") String mode) {
        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (userData.getPartyID() < 1) {
            senderManager.sendMessage(sender, messagesFile.getString("error.party.no-party"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        PartyData partyData = partyManager.getPartyData(sender);

        if (partyData == null){
            return true;
        }

        if (!userData.isPlayerLeader()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.party.no-perms.set"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (mode.isEmpty()) {
            if (!partyData.isPrivate()) {
                partyData.setPrivate(true);
                senderManager.sendMessage(sender, commandFile.getString("commands.party.set.mode")
                        .replace("%mode%", commandFile.getString("commands.party.mode.private")));
                return true;
            }

            partyData.setPrivate(false);
            senderManager.sendMessage(sender, commandFile.getString("commands.party.set.mode")
                    .replace("%mode%", commandFile.getString("commands.party.mode.public")));
            return true;
        }

        if (mode.equalsIgnoreCase("private")) {
            partyData.setPrivate(true);
            senderManager.sendMessage(sender, commandFile.getString("commands.party.set.mode")
                    .replace("%mode%", commandFile.getString("commands.party.mode.private")));
            return true;
        }

        if (mode.equalsIgnoreCase("public")) {
            partyData.setPrivate(false);
            senderManager.sendMessage(sender, commandFile.getString("commands.party.set.mode")
                    .replace("%mode%", commandFile.getString("commands.party.mode.public")));
            return true;
        }

        senderManager.sendMessage(sender, messagesFile.getString("error.unknown-args")
                .replace("%usage%", TextUtils.getUsage("party", "set", "[private/public]")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "info")
    public boolean onInfoSubCommand(@Sender Player sender) {
        PartyData partyData = partyManager.getPartyData(sender);

        if (partyData == null){
            return true;
        }

        List<String> arrayList = new ArrayList<>();

        for (UUID uuid : partyData.getPlayers()) {
            arrayList.add(Bukkit.getPlayer(uuid).getName());
        }

        List<String> partyList = commandFile.getStringList("commands.party.info.format");
        partyList.replaceAll(players -> players
                .replace("%loop-players%", String.join(",", arrayList))
                .replace("%leader%", Bukkit.getPlayer(partyData.getPlayerLeader()).getName()));

        senderManager.sendMessage(sender, partyList);
        return true;
    }

    @Command(names = "invite")
    public boolean onInviteSubCommand(@Sender Player sender, @OptArg("") String target) {

        if (target.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("party", "invite", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player player = Bukkit.getPlayerExact(target);

        if (player == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(player)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        partyManager.invitePlayer(sender, player);
        return true;
    }

    @Command(names = "removeinvite")
    public boolean onRemoveInviteSubCommand(@Sender Player sender, @OptArg("") String target) {
        if (target.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("party", "removeinvite", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player player = Bukkit.getPlayerExact(target);

        if (player == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(player)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        partyManager.removeInvitePlayer(sender, player);
        return true;
    }

    @Command(names = "disband")
    public boolean onDisbandSubCommand(@Sender Player sender) {
        partyManager.deleteParty(sender);
        return true;
    }

    @Command(names = "promote")
    public boolean onPromoteSubCommand(@Sender Player sender, @OptArg("") String target) {
        if (target.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("party", "promote", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player player = Bukkit.getPlayerExact(target);

        if (player == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(player)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        partyManager.promoteToLeader(sender, player);
        return true;
    }

    @Command(names = "kick")
    public boolean onKickSubCommand(@Sender Player sender, @OptArg("") String target) {
        if (target.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("party", "removeinvite", "<player>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Player player = Bukkit.getPlayerExact(target);

        if (player == null) {
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (pluginService.getSupportManager().getVanishSupport().isVanished(player)){
            senderManager.sendMessage(sender, messagesFile.getString("error.player-offline"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        partyManager.kickPlayer(sender, player);
        return true;
    }
}