package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;

public record ScreenAction(
    Kind kind,
    String targetScreenId
) {
    public enum Kind {
        UNHANDLED,
        HANDLED,
        RENDER,
        PUSH,
        REPLACE,
        BACK,
        CLEAR
    }

    public ScreenAction {
        Objects.requireNonNull(kind, "kind must not be null");
    }

    public static ScreenAction unhandled() {
        return new ScreenAction(Kind.UNHANDLED, null);
    }

    public static ScreenAction handled() {
        return new ScreenAction(Kind.HANDLED, null);
    }

    public static ScreenAction render() {
        return new ScreenAction(Kind.RENDER, null);
    }

    public static ScreenAction push(String screenId) {
        return new ScreenAction(Kind.PUSH, requireTarget(screenId));
    }

    public static ScreenAction replace(String screenId) {
        return new ScreenAction(Kind.REPLACE, requireTarget(screenId));
    }

    public static ScreenAction back() {
        return new ScreenAction(Kind.BACK, null);
    }

    public static ScreenAction clear() {
        return new ScreenAction(Kind.CLEAR, null);
    }

    private static String requireTarget(String screenId) {
        Objects.requireNonNull(screenId, "screenId must not be null");
        if (screenId.isBlank()) {
            throw new IllegalArgumentException("screenId must not be blank");
        }
        return screenId;
    }
}
