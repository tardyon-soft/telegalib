package ru.tardyon.botframework.telegram.fsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InMemoryStateStorageTest {

    @Test
    void supportsStateAndDataLifecycle() {
        InMemoryStateStorage storage = new InMemoryStateStorage();
        StateKey key = new StateKey("bot-1", 100L, 42L);

        storage.setState(key, State.of("await_name"));
        storage.putData(key, "name", "Alice");
        storage.putData(key, "step", 1);

        assertEquals("await_name", storage.getState(key).orElseThrow().value());
        assertEquals("Alice", storage.getData(key, "name").orElseThrow());
        assertEquals(2, storage.getData(key).size());

        storage.clearState(key);
        assertTrue(storage.getState(key).isEmpty());
        assertEquals(2, storage.getData(key).size());

        storage.clearData(key);
        assertTrue(storage.getData(key).isEmpty());
    }
}
