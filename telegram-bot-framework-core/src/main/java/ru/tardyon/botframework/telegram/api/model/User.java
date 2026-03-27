package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
    long id,
    @JsonProperty("is_bot") Boolean isBot,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    String username,
    @JsonProperty("language_code") String languageCode,
    @JsonProperty("can_join_groups") Boolean canJoinGroups,
    @JsonProperty("can_read_all_group_messages") Boolean canReadAllGroupMessages,
    @JsonProperty("supports_inline_queries") Boolean supportsInlineQueries
) {
}
