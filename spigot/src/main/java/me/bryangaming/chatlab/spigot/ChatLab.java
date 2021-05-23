package me.bryangaming.chatlab.spigot;

import me.bryangaming.chatlab.common.modules.LoaderModule;
import me.bryangaming.chatlab.spigot.module.SpigotModule;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatLab extends JavaPlugin {


    @Override
    public void onEnable() {

        CommandManager commandManager = new BukkitCommandManager("spigot");

        PartInjector partInjector = new LoaderModule(new SpigotModule(), new BukkitModule()).getInjector();

        AnnotatedCommandTreeBuilder builder = new AnnotatedCommandTreeBuilderImpl(partInjector);

        commandManager.registerCommands(builder.fromClass(new TestCommand()));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
