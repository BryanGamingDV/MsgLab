package me.bryangaming.chatlab.managers.player;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.bryangaming.chatlab.utils.string.VariableUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PlayerStatic {

    private static SenderManager playerMethod;

    private PluginService pluginService;

    public PlayerStatic(PluginService pluginService) {
        this.pluginService = pluginService;

        playerMethod = pluginService.getPlayerManager().getSender();

    }


}
