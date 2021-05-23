package me.bryangaming.chatlab.common.wrapper.factory;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.kyori.text.TranslatableComponent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

// By Yushu
public class CommonSenderFactory<T,S> implements PartFactory {

    private final Class<?> wrapperClass;
    private final Class<T> senderClass;
    private final Class<S> playerClass;
    private final String senderNamespace;

    public CommonSenderFactory(
            Class<?> wrapperClass,
            Class<T> senderClass,
            Class<S> playerClass,
            String senderNamespace) {
        this.wrapperClass = wrapperClass;
        this.senderClass = senderClass;
        this.playerClass = playerClass;
        this.senderNamespace = senderNamespace;
    }

    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        return new CommandPart() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void parse(CommandContext context, ArgumentStack stack) {

                T sender = context.getObject(senderClass, senderNamespace);

                if (sender != null) {
                    if (playerClass.isInstance(sender)) {

                        try {
                            Constructor<?> constructor = wrapperClass.getConstructor(playerClass);
                            context.setValue(this, constructor.newInstance(sender));

                        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        return;
                        } else {
                            throw new ArgumentParseException(TranslatableComponent.of("sender.only-player"));
                        }
                    }
                    throw new ArgumentParseException(TranslatableComponent.of("sender.unknown"));
                }

            };
        }

}