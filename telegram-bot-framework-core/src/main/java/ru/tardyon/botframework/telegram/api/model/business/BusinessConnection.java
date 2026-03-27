package ru.tardyon.botframework.telegram.api.model.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessConnection(
    String id,
    User user,
    @JsonProperty("user_chat_id") Long userChatId,
    Integer date,
    BusinessBotRights rights,
    @JsonProperty("is_enabled") Boolean isEnabled
) {
}
