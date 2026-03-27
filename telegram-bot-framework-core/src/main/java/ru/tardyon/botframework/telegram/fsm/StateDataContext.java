package ru.tardyon.botframework.telegram.fsm;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class StateDataContext {

    private final StateStorage stateStorage;
    private final StateKey stateKey;

    StateDataContext(StateStorage stateStorage, StateKey stateKey) {
        this.stateStorage = Objects.requireNonNull(stateStorage, "stateStorage must not be null");
        this.stateKey = Objects.requireNonNull(stateKey, "stateKey must not be null");
    }

    public void put(String key, Object value) {
        stateStorage.putData(stateKey, key, value);
    }

    public Optional<Object> get(String key) {
        return stateStorage.getData(stateKey, key);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        return get(key).map(value -> {
            if (!type.isInstance(value)) {
                throw new ClassCastException("State data key '" + key + "' is not of type " + type.getName());
            }
            return (T) value;
        });
    }

    public Map<String, Object> asMap() {
        return stateStorage.getData(stateKey);
    }

    public void clear() {
        stateStorage.clearData(stateKey);
    }
}
