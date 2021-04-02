package code.registry;

import code.ChatLab;
import code.PluginService;
import code.commands.*;
import code.commands.modules.CustomLanguage;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.command.Command;

import java.util.List;


public class CommandRegistry implements LoaderService {


    private final PluginService pluginService;
    private final ChatLab plugin;

    private AnnotatedCommandTreeBuilder builder;
    private CommandManager commandManager;

    public CommandRegistry(ChatLab plugin, PluginService pluginService) {
        this.plugin = plugin;
        this.pluginService = pluginService;
    }


    @Override
    public void setup() {

        pluginService.getLogs().log("Loading CommandRegistry");

        createCommandManager();

        commandManager.getTranslator().setProvider(new CustomLanguage(pluginService));
        reCheckCommands();

        pluginService.getLogs().log("Commands loaded!");
        plugin.getLogger().info("Commands loaded!");
    }

    public void reCheckCommands(){
        registerCommands(
                new BmsgCommand(plugin, pluginService),
                new MsgCommand(pluginService),
                new ReplyCommand(pluginService),
                new SocialSpyCommand(pluginService),
                new StaffChatCommand(pluginService),
                new HelpopCommand(pluginService),
                new IgnoreCommand(pluginService),
                new UnIgnoreCommand(pluginService),
                new ChatCommand(plugin, pluginService),
                new BroadcastCommand(pluginService),
                new BroadcastWorldCommand(pluginService),
                new ChannelCommand(pluginService),
                new MotdCommand(pluginService),
                new StreamCommand(pluginService),
                new CommandSpyCommand(pluginService));
    }

    public void registerCommands(CommandClass... commandClasses) {

        for (CommandClass commandClass : commandClasses) {

            List<Command> command = builder.fromClass(commandClass);
            String commandName = command.get(0).getName();

            if (pluginService.getPathManager().isCommandEnabled(commandName)) {
                commandManager.registerCommands(command);
                pluginService.getLogs().log("Command: " + commandName + " loaded.");
            } else {
                pluginService.getLogs().log("Command: " + commandName + " unloaded.", 0);
            }
        }
    }

    private void createCommandManager() {
        commandManager = new BukkitCommandManager("ChatLab");

        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());

        builder = new AnnotatedCommandTreeBuilderImpl(injector);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnnotatedCommandTreeBuilder getBuilder() {
        return builder;
    }
}
