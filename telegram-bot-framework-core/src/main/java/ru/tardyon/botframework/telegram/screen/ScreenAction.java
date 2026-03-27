package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;

public record ScreenAction(
    Kind kind,
    String targetScreenId,
    String targetData
) {
    public static final String TARGET_DATA_KEY = "_screen_target_data";

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
        return new ScreenAction(Kind.UNHANDLED, null, null);
    }

    public static ScreenAction handled() {
        return new ScreenAction(Kind.HANDLED, null, null);
    }

    public static ScreenAction render() {
        return new ScreenAction(Kind.RENDER, null, null);
    }

    public static ScreenAction push(String screenId) {
        return new ScreenAction(Kind.PUSH, requireTarget(screenId), null);
    }

    public static ScreenAction push(String screenId, String targetData) {
        return new ScreenAction(Kind.PUSH, requireTarget(screenId), targetData);
    }

    public static ScreenAction replace(String screenId) {
        return new ScreenAction(Kind.REPLACE, requireTarget(screenId), null);
    }

    public static ScreenAction replace(String screenId, String targetData) {
        return new ScreenAction(Kind.REPLACE, requireTarget(screenId), targetData);
    }

    public static ScreenAction back() {
        return new ScreenAction(Kind.BACK, null, null);
    }

    public static ScreenAction clear() {
        return new ScreenAction(Kind.CLEAR, null, null);
    }

    private static String requireTarget(String screenId) {
        Objects.requireNonNull(screenId, "screenId must not be null");
        if (screenId.isBlank()) {
            throw new IllegalArgumentException("screenId must not be blank");
        }
        return screenId;
    }
}
