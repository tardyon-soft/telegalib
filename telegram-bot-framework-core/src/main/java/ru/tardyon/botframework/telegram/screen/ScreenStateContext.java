package ru.tardyon.botframework.telegram.screen;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ScreenStateContext {

    private final ScreenStateStorage storage;
    private final ScreenKey key;

    public ScreenStateContext(ScreenStateStorage storage, ScreenKey key) {
        this.storage = Objects.requireNonNull(storage, "storage must not be null");
        this.key = Objects.requireNonNull(key, "key must not be null");
    }

    public ScreenKey key() {
        return key;
    }

    public Optional<String> currentScreenId() {
        return storage.find(key)
            .flatMap(ScreenStack::current)
            .map(ScreenFrame::screenId);
    }

    public Optional<Integer> renderedMessageId() {
        return storage.find(key).flatMap(ScreenStack::renderedMessageId);
    }

    public void setRenderedMessageId(Integer messageId) {
        storage.getOrCreate(key).setRenderedMessageId(messageId);
    }

    public void putData(String key, Object value) {
        currentFrame().putData(key, value);
    }

    public Optional<Object> getData(String key) {
        return currentFrame().getData(key);
    }

    public Map<String, Object> data() {
        return currentFrame().data();
    }

    public void clearData() {
        currentFrame().clearData();
    }

    private ScreenFrame currentFrame() {
        return storage.getOrCreate(key)
            .current()
            .orElseThrow(() -> new IllegalStateException("No active screen frame for key: " + key));
    }
}
