package ru.tardyon.botframework.telegram.api.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ForceReply(
    @JsonProperty("force_reply") boolean forceReply
) implements ReplyMarkup {
    public ForceReply() {
        this(true);
    }
}
