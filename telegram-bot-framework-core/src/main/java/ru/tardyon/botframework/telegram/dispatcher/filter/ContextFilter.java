package ru.tardyon.botframework.telegram.dispatcher.filter;

import java.util.Objects;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface ContextFilter<E> {

    boolean test(UpdateContext updateContext, E event);

    default ContextFilter<E> and(ContextFilter<? super E> other) {
        Objects.requireNonNull(other, "other must not be null");
        return (updateContext, event) -> this.test(updateContext, event) && other.test(updateContext, event);
    }

    default ContextFilter<E> or(ContextFilter<? super E> other) {
        Objects.requireNonNull(other, "other must not be null");
        return (updateContext, event) -> this.test(updateContext, event) || other.test(updateContext, event);
    }

    default ContextFilter<E> not() {
        return (updateContext, event) -> !this.test(updateContext, event);
    }
}
