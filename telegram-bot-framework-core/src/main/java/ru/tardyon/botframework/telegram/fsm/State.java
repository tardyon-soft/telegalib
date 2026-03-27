package ru.tardyon.botframework.telegram.fsm;

import java.util.Objects;

public record State(String value) {

    public State {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    public static State of(String value) {
        return new State(value);
    }
}
