package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public record SetWebhookRequest(
    String url,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("max_connections") Integer maxConnections,
    @JsonProperty("allowed_updates") List<String> allowedUpdates,
    @JsonProperty("drop_pending_updates") Boolean dropPendingUpdates,
    @JsonProperty("secret_token") String secretToken
) {
    public SetWebhookRequest {
        Objects.requireNonNull(url, "url must not be null");
        if (url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
    }
}
