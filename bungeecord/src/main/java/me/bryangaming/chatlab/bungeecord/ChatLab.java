package me.bryangaming.chatlab.bungeecord;

import me.bryangaming.chatlab.bungeecord.module.BungeeSenderModule;
import me.bryangaming.chatlab.common.modules.LoaderModule;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.bungee.BungeeCommandManager;
import me.fixeddev.commandflow.bungee.factory.BungeeModule;
import net.md_5.bungee.api.plugin.Plugin;

public class ChatLab extends Plugin {
    
    @Override
    public void onEnable() {

        CommandManager commandManager    = new BungeeCommandManager(this);

        PartInjector partInjector = new LoaderModule(new BungeeModule(), new BungeeSenderModule()).getInjector();

        AnnotatedCommandTreeBuilder builder = new AnnotatedCommandTreeBuilderImpl(partInjector);
        commandManager.registerCommands(builder.fromClass(new TestCommand()));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
