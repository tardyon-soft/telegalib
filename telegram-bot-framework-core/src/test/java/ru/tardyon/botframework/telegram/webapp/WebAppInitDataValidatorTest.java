package ru.tardyon.botframework.telegram.webapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class WebAppInitDataValidatorTest {

    private final WebAppInitDataValidator validator = new WebAppInitDataValidator();

    @Test
    void validatesCorrectInitData() throws Exception {
        String botToken = "123456:ABCDEF";
        long authDate = 1_710_000_000L;
        String userJson = "{\"id\":42,\"first_name\":\"Ann\"}";

        String initData = buildSignedInitData(
            botToken,
            Map.of(
                "auth_date", String.valueOf(authDate),
                "query_id", "AAEAAAE",
                "user", userJson
            )
        );

        WebAppInitDataValidationResult result = validator.validate(
            initData,
            botToken,
            Duration.ofHours(1),
            Instant.ofEpochSecond(authDate).plusSeconds(120)
        );

        assertTrue(result.valid());
        assertEquals(authDate, result.authDate());
        assertNotNull(result.authDateInstant());
        assertEquals(userJson, result.fields().get("user"));
    }

    @Test
    void rejectsInitDataWithWrongHash() throws Exception {
        String botToken = "123456:ABCDEF";
        String initData = buildSignedInitData(
            botToken,
            Map.of("auth_date", "1710000000", "query_id", "AAEAAAE", "user", "{\"id\":1}")
        ) + "00";

        WebAppInitDataValidationResult result = validator.validate(initData, botToken);

        assertFalse(result.valid());
        assertEquals("Hash mismatch", result.error());
    }

    @Test
    void rejectsExpiredAuthDateWhenMaxAgeProvided() throws Exception {
        String botToken = "123456:ABCDEF";
        long authDate = 1_710_000_000L;
        String initData = buildSignedInitData(
            botToken,
            Map.of("auth_date", String.valueOf(authDate), "query_id", "AAEAAAE", "user", "{\"id\":1}")
        );

        WebAppInitDataValidationResult result = validator.validate(
            initData,
            botToken,
            Duration.ofMinutes(5),
            Instant.ofEpochSecond(authDate).plus(Duration.ofMinutes(6))
        );

        assertFalse(result.valid());
        assertEquals("auth_date is too old", result.error());
    }

    private static String buildSignedInitData(String botToken, Map<String, String> fields) throws Exception {
        List<Map.Entry<String, String>> sorted = new ArrayList<>(fields.entrySet());
        sorted.sort(Comparator.comparing(Map.Entry::getKey));

        StringBuilder dataCheckString = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<String, String> entry = sorted.get(i);
            dataCheckString.append(entry.getKey()).append('=').append(entry.getValue());
            if (i < sorted.size() - 1) {
                dataCheckString.append('\n');
            }
        }

        byte[] secretKey = hmacSha256("WebAppData".getBytes(StandardCharsets.UTF_8), botToken.getBytes(StandardCharsets.UTF_8));
        byte[] hash = hmacSha256(secretKey, dataCheckString.toString().getBytes(StandardCharsets.UTF_8));
        String hashHex = toHex(hash);

        StringBuilder query = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (index++ > 0) {
                query.append('&');
            }
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            query.append('=');
            query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        query.append("&hash=").append(hashHex);
        return query.toString();
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
}
