package ru.tardyon.botframework.telegram.dispatcher.filter;

import java.util.Objects;

@FunctionalInterface
public interface Filter<E> {

    boolean test(E event);

    default Filter<E> and(Filter<? super E> other) {
        Objects.requireNonNull(other, "other must not be null");
        return event -> this.test(event) && other.test(event);
    }

    default Filter<E> or(Filter<? super E> other) {
        Objects.requireNonNull(other, "other must not be null");
        return event -> this.test(event) || other.test(event);
    }

    default Filter<E> not() {
        return event -> !this.test(event);
    }
}
