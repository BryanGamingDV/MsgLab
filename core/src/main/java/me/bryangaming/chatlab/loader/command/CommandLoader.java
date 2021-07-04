package me.bryangaming.chatlab.loader.command;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Loader;
import me.bryangaming.chatlab.commands.*;
import me.bryangaming.chatlab.commands.translation.CommandTranslation;
import me.bryangaming.chatlab.debug.LoggerTypeEnum;
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


public class CommandLoader implements Loader {


    private final PluginService pluginService;

    private AnnotatedCommandTreeBuilder builder;
    private CommandManager commandManager;

    public CommandLoader(PluginService pluginService) {
        this.pluginService = pluginService;
        load();
    }


    @Override
    public void load() {
        pluginService.getDebugger().log("Loading CommandLoader");

        registerCommandManager();
        pluginService.getDebugger().log("Commands loaded!");
        pluginService.getPlugin().getLogger().info("Commands loaded!");
    }

    public void registerCommands(CommandClass... commandClasses) {

        for (CommandClass commandClass : commandClasses) {

            List<Command> command = builder.fromClass(commandClass);

            String commandName = command.get(0).getName();
            CommandsType commandsType = CommandsType.valueOf(commandName.toUpperCase());

            if (pluginService.getFiles().getConfigFile().getBoolean("modules." + commandsType.getCommandName() + ".enabled")) {
                commandManager.registerCommands(command);
                pluginService.getDebugger().log("Command: " + commandName + " loaded.");
            } else {
                pluginService.getDebugger().log("Command: " + commandName + " unloaded.", LoggerTypeEnum.WARNING);
            }
        }
    }

    private void registerCommandManager() {
        commandManager = new BukkitCommandManager("ChatLab");
        commandManager.getTranslator().setProvider(new CommandTranslation(pluginService));

        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());

        builder = new AnnotatedCommandTreeBuilderImpl(injector);
        reCheckCommands();
    }


    public void reCheckCommands(){
        registerCommands(
                new CLabCommand(pluginService),
                new MsgCommand(pluginService),
                new ReplyCommand(pluginService),
                new SocialSpyCommand(pluginService),
                new StaffChatCommand(pluginService),
                new HelpopCommand(pluginService),
                new IgnoreCommand(pluginService),
                new UnIgnoreCommand(pluginService),
                new ChatCommand(pluginService),
                new BroadcastCommand(pluginService),
                new BroadcastWorldCommand(pluginService),
                new ChannelCommand(pluginService),
                new MotdCommand(pluginService),
                new StreamCommand(pluginService),
                new CommandSpyCommand(pluginService),
                new PartyCommand(pluginService),
                new AnnouncerCommand(pluginService),
                new TagsCommand(pluginService));
    }


    public CommandManager getCommandManager() {
        return commandManager;
    }

}
