package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GetFileRequest(
    @JsonProperty("file_id") String fileId
) {
    public GetFileRequest {
        Objects.requireNonNull(fileId, "fileId must not be null");
        if (fileId.isBlank()) {
            throw new IllegalArgumentException("fileId must not be blank");
        }
    }
}
