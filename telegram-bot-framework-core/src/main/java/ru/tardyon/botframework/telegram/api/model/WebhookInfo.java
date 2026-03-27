package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookInfo(
    String url,
    @JsonProperty("has_custom_certificate") Boolean hasCustomCertificate,
    @JsonProperty("pending_update_count") Integer pendingUpdateCount,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("last_error_date") Integer lastErrorDate,
    @JsonProperty("last_error_message") String lastErrorMessage,
    @JsonProperty("last_synchronization_error_date") Integer lastSynchronizationErrorDate,
    @JsonProperty("max_connections") Integer maxConnections,
    @JsonProperty("allowed_updates") List<String> allowedUpdates
) {
}
