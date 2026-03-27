package ru.tardyon.botframework.telegram.testkit.fixture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.Update;

/**
 * Classpath fixture helpers for canned Telegram payloads.
 */
public final class TelegramJsonFixtures {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TelegramJsonFixtures() {
    }

    public static String loadJson(String resourcePath) {
        String normalized = normalizePath(resourcePath);
        try (InputStream in = TelegramJsonFixtures.class.getResourceAsStream(normalized)) {
            if (in == null) {
                throw new IllegalArgumentException("Fixture not found: " + normalized);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load fixture: " + normalized, e);
        }
    }

    public static <T> T readValue(String resourcePath, Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        String json = loadJson(resourcePath);
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse fixture as " + type.getSimpleName(), e);
        }
    }

    public static Update update(String resourcePath) {
        return readValue(resourcePath, Update.class);
    }

    public static String toJson(Object value) {
        Objects.requireNonNull(value, "value must not be null");
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize fixture object", e);
        }
    }

    private static String normalizePath(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        if (resourcePath.isBlank()) {
            throw new IllegalArgumentException("resourcePath must not be blank");
        }
        return resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
    }
}
