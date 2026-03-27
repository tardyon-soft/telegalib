package ru.tardyon.botframework.telegram.fsm;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class StateContext {

    private final StateStorage stateStorage;
    private final StateKey stateKey;

    public StateContext(StateStorage stateStorage, StateKey stateKey) {
        this.stateStorage = Objects.requireNonNull(stateStorage, "stateStorage must not be null");
        this.stateKey = stateKey;
    }

    public Optional<StateKey> key() {
        return Optional.ofNullable(stateKey);
    }

    public boolean isAvailable() {
        return stateKey != null;
    }

    public void setState(State state) {
        requireStateKey();
        stateStorage.setState(stateKey, Objects.requireNonNull(state, "state must not be null"));
    }

    public void set(String state) {
        setState(State.of(state));
    }

    public void set(State state) {
        setState(state);
    }

    public Optional<State> getState() {
        if (stateKey == null) {
            return Optional.empty();
        }
        return stateStorage.getState(stateKey);
    }

    public Optional<State> get() {
        return getState();
    }

    public void clearState() {
        if (stateKey == null) {
            return;
        }
        stateStorage.clearState(stateKey);
    }

    public void clear() {
        clearState();
        clearData();
    }

    public void putData(String key, Object value) {
        requireStateKey();
        stateStorage.putData(stateKey, key, value);
    }

    public Optional<Object> getData(String key) {
        if (stateKey == null) {
            return Optional.empty();
        }
        return stateStorage.getData(stateKey, key);
    }

    public Map<String, Object> getData() {
        if (stateKey == null) {
            return Map.of();
        }
        return stateStorage.getData(stateKey);
    }

    public void clearData() {
        if (stateKey == null) {
            return;
        }
        stateStorage.clearData(stateKey);
    }

    public StateDataContext data() {
        requireStateKey();
        return new StateDataContext(stateStorage, stateKey);
    }

    private void requireStateKey() {
        if (stateKey == null) {
            throw new IllegalStateException("State key is not available for this update");
        }
    }
}
