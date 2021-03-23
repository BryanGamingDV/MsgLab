package code.registry;

import code.MsgLab;
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


public class CommandRegistry implements LoaderService {


    private final PluginService pluginService;
    private final MsgLab plugin;

    private AnnotatedCommandTreeBuilder builder;
    private CommandManager commandManager;

    public CommandRegistry(MsgLab plugin, PluginService pluginService) {
        this.plugin = plugin;
        this.pluginService = pluginService;
    }


    @Override
    public void setup() {

        pluginService.getLogs().log("Loading CommandRegistry");

        createCommandManager();

        commandManager.getTranslator().setProvider(new CustomLanguage(pluginService));

        registerCommands("bmsg", new BmsgCommand(plugin, pluginService));
        registerCommands("msg", new MsgCommand(pluginService));
        registerCommands("reply", new ReplyCommand(pluginService));
        registerCommands("socialspy", new SocialSpyCommand(pluginService));
        registerCommands("staffchat", new StaffChatCommand(pluginService));
        registerCommands("helpop", new HelpopCommand(pluginService));
        registerCommands("ignore", new IgnoreCommand(pluginService));
        registerCommands("unignore", new UnIgnoreCommand(pluginService));
        registerCommands("chat", new ChatCommand(plugin, pluginService));
        registerCommands("broadcast", new BroadcastCommand(pluginService));
        registerCommands("broadcastworld", new BroadcastWorldCommand(pluginService));
        registerCommands("channel", new ChannelCommand(pluginService));
        registerCommands("motd", new MotdCommand(pluginService));
        registerCommands("stream", new StreamCommand(pluginService));
        registerCommands("commandspy", new CommandSpyCommand(pluginService));

        pluginService.getLogs().log("Commands loaded!");
        plugin.getLogger().info("Commands loaded!");
    }

    public void registerCommands(String commandName, CommandClass commandClass) {
        if (pluginService.getPathManager().isCommandEnabled(commandName)) {
            commandManager.registerCommands(builder.fromClass(commandClass));
            pluginService.getLogs().log("Command: " + commandName + " loaded.");
        } else {
            pluginService.getLogs().log("Command: " + commandName + " unloaded.", 0);
        }
    }

    private void createCommandManager() {
        commandManager = new BukkitCommandManager("MsgLab");

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
