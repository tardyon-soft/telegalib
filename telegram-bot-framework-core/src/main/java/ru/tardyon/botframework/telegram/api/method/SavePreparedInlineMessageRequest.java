package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;

public record SavePreparedInlineMessageRequest(
    @JsonProperty("user_id") Long userId,
    InlineQueryResult result,
    @JsonProperty("allow_user_chats") Boolean allowUserChats,
    @JsonProperty("allow_bot_chats") Boolean allowBotChats,
    @JsonProperty("allow_group_chats") Boolean allowGroupChats,
    @JsonProperty("allow_channel_chats") Boolean allowChannelChats
) {

    public SavePreparedInlineMessageRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(result, "result must not be null");
    }
}
