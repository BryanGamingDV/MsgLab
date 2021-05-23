package me.bryangaming.chatlab.bungeecord.module;

import me.bryangaming.chatlab.bungeecord.impl.ProxiedPlayerImpl;
import me.bryangaming.chatlab.bungeecord.impl.ProxiedSenderImpl;
import me.bryangaming.chatlab.common.wrapper.factory.CommonSenderFactory;
import me.bryangaming.chatlab.common.wrapper.annotation.SenderAnnotWrapper;
import me.bryangaming.chatlab.common.part.TransformingPartFactory;
import me.bryangaming.chatlab.common.wrapper.PlayerWrapper;
import me.bryangaming.chatlab.common.wrapper.SenderWrapper;
import me.fixeddev.commandflow.annotated.part.AbstractModule;
import me.fixeddev.commandflow.annotated.part.Key;;
import me.fixeddev.commandflow.bungee.BungeeCommandManager;
import me.fixeddev.commandflow.bungee.factory.ProxiedPlayerPartFactory;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeSenderModule extends AbstractModule {
    @Override
    public void configure() {
        bindFactory(SenderWrapper.class, new TransformingPartFactory<>(
                new ProxiedPlayerPartFactory(),
                ProxiedSenderImpl::new,
                SenderWrapper.class));

        bindFactory(PlayerWrapper.class, new TransformingPartFactory<>(new ProxiedPlayerPartFactory(),
                ProxiedPlayerImpl::new,
                PlayerWrapper.class));

        bindFactory(new Key(PlayerWrapper.class, SenderAnnotWrapper.class),
                new CommonSenderFactory<>(ProxiedSenderImpl.class,
                        CommandSender.class,
                        ProxiedPlayer.class,
                        BungeeCommandManager.SENDER_NAMESPACE));
    }
}
