package ru.tardyon.botframework.telegram.api.model.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.Chat;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessMessagesDeleted(
    @JsonProperty("business_connection_id") String businessConnectionId,
    Chat chat,
    @JsonProperty("message_ids") List<Integer> messageIds
) {
}
