package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateChatSubscriptionInviteLinkRequest(
    @JsonProperty("chat_id") Object chatId,
    String name,
    @JsonProperty("subscription_period") Integer subscriptionPeriod,
    @JsonProperty("subscription_price") Integer subscriptionPrice
) {

    public CreateChatSubscriptionInviteLinkRequest {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId must not be null");
        }
        if (name != null && name.length() > 32) {
            throw new IllegalArgumentException("name length must be in range 0..32");
        }
        if (subscriptionPeriod == null) {
            throw new IllegalArgumentException("subscriptionPeriod must not be null");
        }
        if (subscriptionPeriod != 2592000) {
            throw new IllegalArgumentException("subscriptionPeriod must be 2592000");
        }
        if (subscriptionPrice == null) {
            throw new IllegalArgumentException("subscriptionPrice must not be null");
        }
        if (subscriptionPrice < 1 || subscriptionPrice > 10000) {
            throw new IllegalArgumentException("subscriptionPrice must be in range 1..10000");
        }
    }
}
