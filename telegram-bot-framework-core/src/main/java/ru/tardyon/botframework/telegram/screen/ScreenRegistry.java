package ru.tardyon.botframework.telegram.screen;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ScreenRegistry {

    private final ConcurrentMap<String, Screen> screens = new ConcurrentHashMap<>();

    public ScreenRegistry register(Screen screen) {
        Objects.requireNonNull(screen, "screen must not be null");
        String id = requireScreenId(screen.id());
        Screen previous = screens.putIfAbsent(id, screen);
        if (previous != null) {
            throw new IllegalStateException("Screen already registered: " + id);
        }
        return this;
    }

    public ScreenRegistry registerAll(Collection<? extends Screen> screens) {
        Objects.requireNonNull(screens, "screens must not be null");
        for (Screen screen : screens) {
            register(screen);
        }
        return this;
    }

    public Optional<Screen> find(String screenId) {
        return Optional.ofNullable(screens.get(screenId));
    }

    public Screen getRequired(String screenId) {
        String id = requireScreenId(screenId);
        Screen screen = screens.get(id);
        if (screen == null) {
            throw new IllegalStateException("Screen is not registered: " + id);
        }
        return screen;
    }

    private static String requireScreenId(String screenId) {
        Objects.requireNonNull(screenId, "screenId must not be null");
        if (screenId.isBlank()) {
            throw new IllegalArgumentException("screenId must not be blank");
        }
        return screenId;
    }
}
