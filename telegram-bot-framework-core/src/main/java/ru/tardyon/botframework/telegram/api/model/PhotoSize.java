package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PhotoSize(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    Integer width,
    Integer height,
    @JsonProperty("file_size") Long fileSize
) {
}

