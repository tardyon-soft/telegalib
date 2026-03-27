package ru.tardyon.botframework.telegram.testkit.update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiResponse;

/**
 * Deterministic getUpdates simulator for fake Bot API tests.
 */
public final class PollingUpdateSimulator {

    private final List<Update> updates = new ArrayList<>();

    public synchronized PollingUpdateSimulator enqueue(Update update) {
        updates.add(Objects.requireNonNull(update, "update must not be null"));
        return this;
    }

    public synchronized PollingUpdateSimulator enqueueAll(List<Update> updates) {
        Objects.requireNonNull(updates, "updates must not be null");
        updates.forEach(this::enqueue);
        return this;
    }

    public synchronized void clear() {
        updates.clear();
    }

    public synchronized int size() {
        return updates.size();
    }

    public synchronized FakeBotApiResponse buildGetUpdatesResponse(String rawBody, ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper, "objectMapper must not be null");

        GetUpdatesRequest request = parseGetUpdatesRequest(rawBody, objectMapper);
        Integer offset = request == null ? null : request.offset();
        Integer limit = request == null ? null : request.limit();

        List<Update> filtered = updates.stream()
            .filter(update -> offset == null || update.updateId() == null || update.updateId() >= offset)
            .toList();

        if (limit != null && limit > 0 && filtered.size() > limit) {
            filtered = filtered.subList(0, limit);
        }

        try {
            return FakeBotApiResponse.okJson(objectMapper.writeValueAsString(filtered));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize getUpdates fake response", e);
        }
    }

    private static GetUpdatesRequest parseGetUpdatesRequest(String rawBody, ObjectMapper objectMapper) {
        if (rawBody == null || rawBody.isBlank()) {
            return new GetUpdatesRequest();
        }
        try {
            return objectMapper.readValue(rawBody, GetUpdatesRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid getUpdates request body for simulator", e);
        }
    }
}
