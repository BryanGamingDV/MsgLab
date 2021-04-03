package org.bryangaming.chatlab.revisor;

import org.bryangaming.chatlab.ChatLab;
import org.bryangaming.chatlab.PluginService;
import org.bryangaming.chatlab.revisor.commands.BlockRevisor;
import org.bryangaming.chatlab.revisor.message.*;
import org.bryangaming.chatlab.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RevisorManager {


    private PluginService pluginService;

    private ChatLab plugin;
    private ModuleCheck path;

    private CooldownData cooldownData;
    private MentionRevisor mentionRevisor;
    private FloodRevisor floodRevisor;
    private WordRevisor wordRevisor;
    private LinkRevisor linkRevisor;
    private CapsRevisor capsRevisor;
    private DotRevisor dotRevisor;

    private BlockRevisor blockRevisor;

    private int revisorLevel = 2;

    public RevisorManager(PluginService pluginService) {
        this.pluginService = pluginService;
        pluginService.getListManager().getModules().add("chat_revisor");
        plugin = pluginService.getPlugin();
        path = pluginService.getPathManager();
        setup();
    }

    private void setup() {
        cooldownData = new CooldownData(pluginService);
        mentionRevisor = new MentionRevisor(pluginService);
        floodRevisor = new FloodRevisor(pluginService);
        wordRevisor = new WordRevisor(pluginService);
        linkRevisor = new LinkRevisor(pluginService);
        capsRevisor = new CapsRevisor(pluginService);
        dotRevisor = new DotRevisor(pluginService);

        blockRevisor = new BlockRevisor(pluginService);

    }

    public void setLevel(int level) {
        revisorLevel = level;
    }

    public String revisor(UUID uuid, String message) {
        if (!(path.isOptionEnabled("chat_revisor"))) {
            return message;
        }

        if (revisorLevel == 0) {
            return message;
        }

        Player player = Bukkit.getPlayer(uuid);

        message = getMentionRevisor().check(message);
        message = getBadWordsRevisor().check(player, message);
        message = getAntiFloodRevisor().check(player, message);
        message = getCapsRevisor().check(player, message);

        if (revisorLevel > 1) {
            message = getLinkRevisor().check(player, message);
            if (message == null) return null;
        }

        revisorLevel = 2;
        return getDotRevisor().check(player, message);
    }

    public String revisorCMD(UUID uuid, String command) {
        Player player = Bukkit.getPlayer(uuid);

        command = blockRevisor.revisor(player, command);

        if (command == null) {
            return null;
        }

        return command;
    }

    public BlockRevisor getBlockRevisor() {
        return blockRevisor;
    }

    public MentionRevisor getMentionRevisor() {
        return mentionRevisor;
    }

    public LinkRevisor getLinkRevisor() {
        return linkRevisor;
    }

    public FloodRevisor getAntiFloodRevisor() {
        return floodRevisor;
    }

    public CapsRevisor getCapsRevisor() {
        return capsRevisor;
    }

    public CooldownData getAntiRepeatRevisor() {
        return cooldownData;
    }

    public WordRevisor getBadWordsRevisor() {
        return wordRevisor;
    }

    public DotRevisor getDotRevisor() {
        return dotRevisor;
    }
}
