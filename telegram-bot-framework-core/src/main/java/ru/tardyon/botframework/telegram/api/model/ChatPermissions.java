package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatPermissions(
    @JsonProperty("can_send_messages") Boolean canSendMessages,
    @JsonProperty("can_send_audios") Boolean canSendAudios,
    @JsonProperty("can_send_documents") Boolean canSendDocuments,
    @JsonProperty("can_send_photos") Boolean canSendPhotos,
    @JsonProperty("can_send_videos") Boolean canSendVideos,
    @JsonProperty("can_send_video_notes") Boolean canSendVideoNotes,
    @JsonProperty("can_send_voice_notes") Boolean canSendVoiceNotes,
    @JsonProperty("can_send_polls") Boolean canSendPolls,
    @JsonProperty("can_send_other_messages") Boolean canSendOtherMessages,
    @JsonProperty("can_add_web_page_previews") Boolean canAddWebPagePreviews,
    @JsonProperty("can_edit_tag") Boolean canEditTag,
    @JsonProperty("can_change_info") Boolean canChangeInfo,
    @JsonProperty("can_invite_users") Boolean canInviteUsers,
    @JsonProperty("can_pin_messages") Boolean canPinMessages,
    @JsonProperty("can_manage_topics") Boolean canManageTopics
) {
}
