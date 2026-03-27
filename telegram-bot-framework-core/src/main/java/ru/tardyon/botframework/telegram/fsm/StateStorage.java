package ru.tardyon.botframework.telegram.fsm;

import java.util.Map;
import java.util.Optional;

public interface StateStorage {

    void setState(StateKey key, State state);

    Optional<State> getState(StateKey key);

    void clearState(StateKey key);

    void putData(StateKey key, String dataKey, Object value);

    Optional<Object> getData(StateKey key, String dataKey);

    Map<String, Object> getData(StateKey key);

    void clearData(StateKey key);
}
