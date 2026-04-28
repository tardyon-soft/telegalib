package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.ChatInviteLink;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberUpdated(
    Chat chat,
    User from,
    Long date,
    @JsonProperty("old_chat_member") ChatMember oldChatMember,
    @JsonProperty("new_chat_member") ChatMember newChatMember,
    @JsonProperty("invite_link") ChatInviteLink inviteLink,
    @JsonProperty("via_join_request") Boolean viaJoinRequest,
    @JsonProperty("via_chat_folder_invite_link") Boolean viaChatFolderInviteLink
) {
}
