package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.command.BotCommandScope;

public record GetMyCommandsRequest(
    BotCommandScope scope,
    @JsonProperty("language_code") String languageCode
) {
}
