package ru.tardyon.botframework.telegram.api.model.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessBotRights(
    @JsonProperty("can_reply") Boolean canReply,
    @JsonProperty("can_read_messages") Boolean canReadMessages,
    @JsonProperty("can_delete_sent_messages") Boolean canDeleteSentMessages,
    @JsonProperty("can_delete_all_messages") Boolean canDeleteAllMessages,
    @JsonProperty("can_edit_name") Boolean canEditName,
    @JsonProperty("can_edit_bio") Boolean canEditBio,
    @JsonProperty("can_edit_profile_photo") Boolean canEditProfilePhoto,
    @JsonProperty("can_edit_username") Boolean canEditUsername,
    @JsonProperty("can_change_gift_settings") Boolean canChangeGiftSettings,
    @JsonProperty("can_view_gifts_and_stars") Boolean canViewGiftsAndStars,
    @JsonProperty("can_convert_gifts_to_stars") Boolean canConvertGiftsToStars,
    @JsonProperty("can_transfer_and_upgrade_gifts") Boolean canTransferAndUpgradeGifts,
    @JsonProperty("can_transfer_stars") Boolean canTransferStars,
    @JsonProperty("can_manage_stories") Boolean canManageStories
) {
}
