package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GetUpdatesRequest(
    Integer offset,
    Integer limit,
    Integer timeout,
    @JsonProperty("allowed_updates") List<String> allowedUpdates
) {
    public GetUpdatesRequest() {
        this(null, null, null, null);
    }
}
