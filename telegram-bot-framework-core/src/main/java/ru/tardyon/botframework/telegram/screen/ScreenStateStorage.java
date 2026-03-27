package ru.tardyon.botframework.telegram.screen;

import java.util.Optional;

public interface ScreenStateStorage {

    Optional<ScreenStack> find(ScreenKey key);

    ScreenStack getOrCreate(ScreenKey key);

    void save(ScreenKey key, ScreenStack stack);

    void clear(ScreenKey key);
}
