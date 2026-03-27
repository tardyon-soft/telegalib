package ru.tardyon.botframework.telegram.api.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SwitchInlineQueryChosenChat(
    String query,
    @JsonProperty("allow_user_chats") Boolean allowUserChats,
    @JsonProperty("allow_bot_chats") Boolean allowBotChats,
    @JsonProperty("allow_group_chats") Boolean allowGroupChats,
    @JsonProperty("allow_channel_chats") Boolean allowChannelChats
) {
}
