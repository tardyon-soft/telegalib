package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CallbackQuery(
    String id,
    User from,
    @JsonDeserialize(using = MaybeInaccessibleMessage.MaybeInaccessibleMessageDeserializer.class)
    MaybeInaccessibleMessage message,
    @JsonProperty("inline_message_id") String inlineMessageId,
    @JsonProperty("chat_instance") String chatInstance,
    String data,
    @JsonProperty("game_short_name") String gameShortName
) {
}
