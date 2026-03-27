package ru.tardyon.botframework.telegram.fsm;

import java.util.Objects;

/**
 * FSM identity key scoped by bot instance + chat + user.
 */
public record StateKey(
    String botId,
    long chatId,
    long userId
) {

    public StateKey {
        Objects.requireNonNull(botId, "botId must not be null");
        if (botId.isBlank()) {
            throw new IllegalArgumentException("botId must not be blank");
        }
    }
}
