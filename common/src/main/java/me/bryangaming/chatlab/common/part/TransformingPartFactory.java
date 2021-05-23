package me.bryangaming.chatlab.common.part;


import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.kyori.text.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


//
// Credits to Yushuu
//

public class TransformingPartFactory<T, R>
        implements PartFactory {

    private final PartFactory delegate;
    private final Function<T, R> transformer;
    private final Class<R> newType;

    public TransformingPartFactory(
            PartFactory delegate,
            Function<T, R> transformer,
            Class<R> newType
    ) {
        this.delegate = delegate;
        this.transformer = transformer;
        this.newType = newType;
    }

    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        CommandPart part = delegate.createPart(name, modifiers);
        if (!(part instanceof ArgumentPart)) {
            throw new IllegalArgumentException("The created command part must be an ArgumentPart!");
        }
        return new TransformingPart((ArgumentPart) part);
    }

    private class TransformingPart implements ArgumentPart {

        private final ArgumentPart delegate;

        private TransformingPart(ArgumentPart delegate) {
            this.delegate = delegate;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<?> parseValue(CommandContext context, ArgumentStack stack, CommandPart caller)
                throws ArgumentParseException {
            List<?> values = delegate.parseValue(context, stack, caller);
            List<R> transformedValues = new ArrayList<>(values.size());
            for (Object value : values) {
                transformedValues.add(transformer.apply((T) value));
            }
            return transformedValues;
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public boolean isAsync() {
            return delegate.isAsync();
        }

        @Override
        @Deprecated
        public List<?> parseValue(CommandContext context, ArgumentStack stack) {
            throw new UnsupportedOperationException("Use parseValue(CommandContext, ArgumentStack, CommandPart) instead");
        }

        @Override
        public Component getLineRepresentation() {
            return delegate.getLineRepresentation();
        }

        @Override
        public List<String> getSuggestions(CommandContext context, ArgumentStack stack) {
            return delegate.getSuggestions(context, stack);
        }

        @Override
        public Type getType() {
            return newType;
        }

    }

}