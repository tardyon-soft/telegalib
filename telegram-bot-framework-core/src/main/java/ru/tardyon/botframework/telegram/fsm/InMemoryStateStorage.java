package ru.tardyon.botframework.telegram.fsm;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryStateStorage implements StateStorage {

    private final ConcurrentMap<StateKey, Entry> storage = new ConcurrentHashMap<>();

    @Override
    public void setState(StateKey key, State state) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(state, "state must not be null");
        storage.computeIfAbsent(key, ignored -> new Entry()).state = state;
    }

    @Override
    public Optional<State> getState(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        Entry entry = storage.get(key);
        return entry == null ? Optional.empty() : Optional.ofNullable(entry.state);
    }

    @Override
    public void clearState(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        storage.computeIfPresent(key, (ignored, entry) -> {
            entry.state = null;
            return entry.isEmpty() ? null : entry;
        });
    }

    @Override
    public void putData(StateKey key, String dataKey, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(dataKey, "dataKey must not be null");
        if (dataKey.isBlank()) {
            throw new IllegalArgumentException("dataKey must not be blank");
        }

        storage.compute(key, (ignored, entry) -> {
            Entry actual = entry == null ? new Entry() : entry;
            if (value == null) {
                actual.data.remove(dataKey);
            } else {
                actual.data.put(dataKey, value);
            }
            return actual.isEmpty() ? null : actual;
        });
    }

    @Override
    public Optional<Object> getData(StateKey key, String dataKey) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(dataKey, "dataKey must not be null");
        Entry entry = storage.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entry.data.get(dataKey));
    }

    @Override
    public Map<String, Object> getData(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        Entry entry = storage.get(key);
        if (entry == null) {
            return Map.of();
        }
        return Map.copyOf(entry.data);
    }

    @Override
    public void clearData(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        storage.computeIfPresent(key, (ignored, entry) -> {
            entry.data.clear();
            return entry.isEmpty() ? null : entry;
        });
    }

    private static final class Entry {
        private volatile State state;
        private final ConcurrentMap<String, Object> data = new ConcurrentHashMap<>();

        private boolean isEmpty() {
            return state == null && data.isEmpty();
        }
    }
}
