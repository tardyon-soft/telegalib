package ru.tardyon.botframework.telegram.webapp;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class WebAppInitDataValidator {

    private static final String HASH_FIELD = "hash";
    private static final String WEB_APP_DATA_KEY = "WebAppData";

    public WebAppInitDataValidationResult validate(String rawInitData, String botToken) {
        return validate(rawInitData, botToken, null, Instant.now());
    }

    public WebAppInitDataValidationResult validate(String rawInitData, String botToken, Duration maxAge) {
        return validate(rawInitData, botToken, maxAge, Instant.now());
    }

    public WebAppInitDataValidationResult validate(String rawInitData, String botToken, Duration maxAge, Instant now) {
        Objects.requireNonNull(rawInitData, "rawInitData must not be null");
        Objects.requireNonNull(botToken, "botToken must not be null");
        Objects.requireNonNull(now, "now must not be null");

        Map<String, String> parsed = parseInitData(rawInitData);
        String hash = parsed.get(HASH_FIELD);
        if (hash == null || hash.isBlank()) {
            return invalid(parsed, "Missing hash field", null);
        }

        String dataCheckString = buildDataCheckString(parsed);
        String expectedHash = calculateDataCheckHash(botToken, dataCheckString);
        if (!constantTimeEquals(expectedHash, hash)) {
            return invalid(parsed, "Hash mismatch", parseAuthDate(parsed));
        }

        Long authDate = parseAuthDate(parsed);
        if (maxAge != null) {
            if (authDate == null) {
                return invalid(parsed, "Missing auth_date for maxAge validation", null);
            }
            Instant authInstant = Instant.ofEpochSecond(authDate);
            if (authInstant.plus(maxAge).isBefore(now)) {
                return invalid(parsed, "auth_date is too old", authDate);
            }
        }

        return new WebAppInitDataValidationResult(
            true,
            Collections.unmodifiableMap(parsed),
            authDate,
            authDate == null ? null : Instant.ofEpochSecond(authDate),
            null
        );
    }

    public Map<String, String> parseInitData(String rawInitData) {
        Objects.requireNonNull(rawInitData, "rawInitData must not be null");

        Map<String, String> result = new LinkedHashMap<>();
        if (rawInitData.isBlank()) {
            return result;
        }

        String[] pairs = rawInitData.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            int eqIndex = pair.indexOf('=');
            String rawKey;
            String rawValue;
            if (eqIndex < 0) {
                rawKey = pair;
                rawValue = "";
            } else {
                rawKey = pair.substring(0, eqIndex);
                rawValue = pair.substring(eqIndex + 1);
            }
            String key = URLDecoder.decode(rawKey, StandardCharsets.UTF_8);
            String value = URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
            result.put(key, value);
        }
        return result;
    }

    private static String buildDataCheckString(Map<String, String> fields) {
        List<Map.Entry<String, String>> entries = new ArrayList<>(fields.entrySet());
        entries.removeIf(entry -> HASH_FIELD.equals(entry.getKey()));
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, String> entry = entries.get(i);
            builder.append(entry.getKey()).append('=').append(entry.getValue());
            if (i < entries.size() - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static String calculateDataCheckHash(String botToken, String dataCheckString) {
        try {
            byte[] secretKey = hmacSha256(WEB_APP_DATA_KEY.getBytes(StandardCharsets.UTF_8), botToken.getBytes(StandardCharsets.UTF_8));
            byte[] hashBytes = hmacSha256(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8));
            return toHex(hashBytes);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate WebApp initData", e);
        }
    }

    private static byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        byte[] leftBytes = left.getBytes(StandardCharsets.UTF_8);
        byte[] rightBytes = right.getBytes(StandardCharsets.UTF_8);
        if (leftBytes.length != rightBytes.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < leftBytes.length; i++) {
            diff |= leftBytes[i] ^ rightBytes[i];
        }
        return diff == 0;
    }

    private static Long parseAuthDate(Map<String, String> parsed) {
        String value = parsed.get("auth_date");
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static WebAppInitDataValidationResult invalid(Map<String, String> fields, String error, Long authDate) {
        return new WebAppInitDataValidationResult(
            false,
            Collections.unmodifiableMap(fields),
            authDate,
            authDate == null ? null : Instant.ofEpochSecond(authDate),
            error
        );
    }
}
