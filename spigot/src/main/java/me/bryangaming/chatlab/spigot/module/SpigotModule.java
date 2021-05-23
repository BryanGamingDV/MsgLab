package me.bryangaming.chatlab.spigot.module;

import me.bryangaming.chatlab.common.wrapper.factory.CommonSenderFactory;
import me.bryangaming.chatlab.common.wrapper.annotation.SenderAnnotWrapper;
import me.bryangaming.chatlab.common.part.TransformingPartFactory;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.SenderWrapper;
import me.bryangaming.chatlab.spigot.impl.PlayerWrapperImpl;
import me.bryangaming.chatlab.spigot.impl.SenderWrapperImpl;
import me.fixeddev.commandflow.annotated.part.AbstractModule;
import me.fixeddev.commandflow.annotated.part.Key;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.CommandSenderFactory;
import me.fixeddev.commandflow.bukkit.factory.PlayerPartFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class  SpigotModule extends AbstractModule {

    @Override
    public void configure() {
        bindFactory(SenderWrapper.class, new TransformingPartFactory<>(
                new CommandSenderFactory(),
                SenderWrapperImpl::new,
                SenderWrapper.class));

        bindFactory(PlayerWrapper.class, new TransformingPartFactory<>(
                new PlayerPartFactory(),
                PlayerWrapperImpl::new,
                PlayerWrapper.class));

        bindFactory(new Key(PlayerWrapper.class, SenderAnnotWrapper.class), new CommonSenderFactory<>(
                        PlayerWrapperImpl.class,
                        CommandSender.class,
                        Player.class,
                        BukkitCommandManager.SENDER_NAMESPACE));
    }
}
