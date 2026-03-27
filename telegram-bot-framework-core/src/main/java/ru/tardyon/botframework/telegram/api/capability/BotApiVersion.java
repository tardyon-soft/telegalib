package ru.tardyon.botframework.telegram.api.capability;

import java.util.Objects;

/**
 * Declared Telegram Bot API version in major.minor form.
 */
public record BotApiVersion(int major, int minor) implements Comparable<BotApiVersion> {

    public static final BotApiVersion V2_0 = of(2, 0);
    public static final BotApiVersion V3_0 = of(3, 0);
    public static final BotApiVersion V6_0 = of(6, 0);
    public static final BotApiVersion V7_2 = of(7, 2);
    public static final BotApiVersion V7_4 = of(7, 4);
    public static final BotApiVersion V7_6 = of(7, 6);
    public static final BotApiVersion V8_0 = of(8, 0);
    public static final BotApiVersion V9_1 = of(9, 1);
    public static final BotApiVersion V9_3 = of(9, 3);
    public static final BotApiVersion V9_5 = of(9, 5);

    public BotApiVersion {
        if (major < 0) {
            throw new IllegalArgumentException("major must be non-negative");
        }
        if (minor < 0) {
            throw new IllegalArgumentException("minor must be non-negative");
        }
    }

    public static BotApiVersion of(int major, int minor) {
        return new BotApiVersion(major, minor);
    }

    public static BotApiVersion parse(String value) {
        Objects.requireNonNull(value, "value must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("version must not be blank");
        }
        String[] parts = trimmed.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("version must match 'major.minor'");
        }
        try {
            return of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("version must match 'major.minor'", ex);
        }
    }

    @Override
    public int compareTo(BotApiVersion other) {
        int majorCompare = Integer.compare(major, other.major);
        if (majorCompare != 0) {
            return majorCompare;
        }
        return Integer.compare(minor, other.minor);
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }
}
