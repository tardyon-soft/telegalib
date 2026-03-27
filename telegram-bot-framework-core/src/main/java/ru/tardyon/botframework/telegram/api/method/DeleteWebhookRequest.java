package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteWebhookRequest(
    @JsonProperty("drop_pending_updates") Boolean dropPendingUpdates
) {
}
