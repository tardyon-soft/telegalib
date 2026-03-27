package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.command.BotCommandScope;

public record SetMyCommandsRequest(
    List<BotCommand> commands,
    BotCommandScope scope,
    @JsonProperty("language_code") String languageCode
) {
    public SetMyCommandsRequest {
        Objects.requireNonNull(commands, "commands must not be null");
        if (commands.size() > 100) {
            throw new IllegalArgumentException("commands size must be <= 100");
        }
        commands = List.copyOf(commands);
    }
}
