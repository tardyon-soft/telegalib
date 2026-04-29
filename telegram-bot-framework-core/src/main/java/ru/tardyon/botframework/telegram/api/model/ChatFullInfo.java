package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.payment.AcceptedGiftTypes;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatFullInfo(
    long id,
    String type,
    String title,
    String username,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("is_forum") Boolean isForum,
    @JsonProperty("is_direct_messages") Boolean isDirectMessages,
    @JsonProperty("accent_color_id") Integer accentColorId,
    @JsonProperty("max_reaction_count") Integer maxReactionCount,
    ChatPhoto photo,
    @JsonProperty("active_usernames") List<String> activeUsernames,
    Object birthdate,
    @JsonProperty("business_intro") Object businessIntro,
    @JsonProperty("business_location") Object businessLocation,
    @JsonProperty("business_opening_hours") Object businessOpeningHours,
    @JsonProperty("personal_chat") Chat personalChat,
    @JsonProperty("parent_chat") Chat parentChat,
    @JsonProperty("available_reactions") List<Object> availableReactions,
    @JsonProperty("background_custom_emoji_id") String backgroundCustomEmojiId,
    @JsonProperty("profile_accent_color_id") Integer profileAccentColorId,
    @JsonProperty("profile_background_custom_emoji_id") String profileBackgroundCustomEmojiId,
    @JsonProperty("emoji_status_custom_emoji_id") String emojiStatusCustomEmojiId,
    @JsonProperty("emoji_status_expiration_date") Integer emojiStatusExpirationDate,
    String bio,
    @JsonProperty("has_private_forwards") Boolean hasPrivateForwards,
    @JsonProperty("has_restricted_voice_and_video_messages") Boolean hasRestrictedVoiceAndVideoMessages,
    @JsonProperty("join_to_send_messages") Boolean joinToSendMessages,
    @JsonProperty("join_by_request") Boolean joinByRequest,
    String description,
    @JsonProperty("invite_link") String inviteLink,
    @JsonProperty("pinned_message") Message pinnedMessage,
    Object permissions,
    @JsonProperty("accepted_gift_types") AcceptedGiftTypes acceptedGiftTypes,
    @JsonProperty("can_send_paid_media") Boolean canSendPaidMedia,
    @JsonProperty("slow_mode_delay") Integer slowModeDelay,
    @JsonProperty("unrestrict_boost_count") Integer unrestrictBoostCount,
    @JsonProperty("message_auto_delete_time") Integer messageAutoDeleteTime,
    @JsonProperty("has_aggressive_anti_spam_enabled") Boolean hasAggressiveAntiSpamEnabled,
    @JsonProperty("has_hidden_members") Boolean hasHiddenMembers,
    @JsonProperty("has_protected_content") Boolean hasProtectedContent,
    @JsonProperty("has_visible_history") Boolean hasVisibleHistory,
    @JsonProperty("sticker_set_name") String stickerSetName,
    @JsonProperty("can_set_sticker_set") Boolean canSetStickerSet,
    @JsonProperty("custom_emoji_sticker_set_name") String customEmojiStickerSetName,
    @JsonProperty("linked_chat_id") Long linkedChatId,
    Object location,
    Object rating,
    @JsonProperty("first_profile_audio") Object firstProfileAudio,
    @JsonProperty("unique_gift_colors") Object uniqueGiftColors,
    @JsonProperty("paid_message_star_count") Integer paidMessageStarCount
) {
}
