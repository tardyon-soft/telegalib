package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryScreenStateStorage implements ScreenStateStorage {

    private final ConcurrentMap<ScreenKey, ScreenStack> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<ScreenStack> find(ScreenKey key) {
        Objects.requireNonNull(key, "key must not be null");
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public ScreenStack getOrCreate(ScreenKey key) {
        Objects.requireNonNull(key, "key must not be null");
        return storage.computeIfAbsent(key, ignored -> new ScreenStack());
    }

    @Override
    public void save(ScreenKey key, ScreenStack stack) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(stack, "stack must not be null");
        storage.put(key, stack);
    }

    @Override
    public void clear(ScreenKey key) {
        Objects.requireNonNull(key, "key must not be null");
        storage.remove(key);
    }
}
