package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatInviteLink(
    @JsonProperty("invite_link") String inviteLink,
    User creator,
    @JsonProperty("creates_join_request") Boolean createsJoinRequest,
    @JsonProperty("is_primary") Boolean isPrimary,
    @JsonProperty("is_revoked") Boolean isRevoked,
    String name,
    @JsonProperty("expire_date") Integer expireDate,
    @JsonProperty("member_limit") Integer memberLimit,
    @JsonProperty("pending_join_request_count") Integer pendingJoinRequestCount,
    @JsonProperty("subscription_period") Integer subscriptionPeriod,
    @JsonProperty("subscription_price") Integer subscriptionPrice
) {
}
