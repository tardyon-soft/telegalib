package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoNote(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    Integer length,
    Integer duration,
    PhotoSize thumbnail,
    @JsonProperty("file_size") Long fileSize
) {
}
