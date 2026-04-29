package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatPhoto(
        @JsonProperty("small_file_id") String smallFileId,
        @JsonProperty("small_file_unique_id") String smallFileUniqueId,
        @JsonProperty("big_file_id") String bigFileId,
        @JsonProperty("big_file_unique_id") String bigFileUniqueId
) {
}