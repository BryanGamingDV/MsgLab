package me.bryangaming.chatlab.loader.command.usage;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.text.TextUtils;
import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.command.Command;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.usage.UsageBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.fixeddev.commandflow.part.Parts.*;

public class CommandUsageBuilder implements UsageBuilder {

    private final Configuration messagesFile;

    public CommandUsageBuilder(PluginService pluginService){
        messagesFile = pluginService.getFiles().getMessagesFile();
    }

    @Override
    public Component getUsage(CommandContext commandContext) {

        CommandUsage commandUsage;
        try {
            commandUsage = CommandUsage.valueOf(commandContext.getLabels().get(0).toUpperCase());
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }


        if (commandContext.getLabels().size() < 2) {
            return TextUtils.convertBasicComponent(messagesFile.getString("global-errors.no-args")
                    .replace("%usage%", commandUsage.getUsage()));
        }

        List<String> arguments = new ArrayList<>(commandContext.getLabels());
        arguments.remove(0);

        String usage = commandUsage.getUsage()
                            .replace("%arg%", String.join(" ", arguments));

        return TextUtils.convertBasicComponent(messagesFile.getString("global-errors.no-args")
                .replace("%usage%", usage));
    }
}
