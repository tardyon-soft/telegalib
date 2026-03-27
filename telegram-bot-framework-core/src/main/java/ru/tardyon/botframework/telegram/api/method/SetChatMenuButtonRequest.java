package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;

public record SetChatMenuButtonRequest(
    @JsonProperty("chat_id") Long chatId,
    @JsonProperty("menu_button") MenuButton menuButton
) {
}
