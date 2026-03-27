package ru.tardyon.botframework.telegram.screen;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ScreenFrame {

    private final String screenId;
    private final ConcurrentMap<String, Object> data = new ConcurrentHashMap<>();

    public ScreenFrame(String screenId) {
        this.screenId = requireScreenId(screenId);
    }

    public String screenId() {
        return screenId;
    }

    public void putData(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (value == null) {
            data.remove(key);
            return;
        }
        data.put(key, value);
    }

    public Optional<Object> getData(String key) {
        Objects.requireNonNull(key, "key must not be null");
        return Optional.ofNullable(data.get(key));
    }

    public Map<String, Object> data() {
        return Map.copyOf(data);
    }

    public void clearData() {
        data.clear();
    }

    private static String requireScreenId(String screenId) {
        Objects.requireNonNull(screenId, "screenId must not be null");
        if (screenId.isBlank()) {
            throw new IllegalArgumentException("screenId must not be blank");
        }
        return screenId;
    }
}
