package ru.tardyon.botframework.telegram.spring.boot.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.fsm.State;
import ru.tardyon.botframework.telegram.fsm.StateKey;
import ru.tardyon.botframework.telegram.fsm.StateStorage;

public final class RedisStateStorage implements StateStorage {

    private static final String STATE_FIELD = "__state";
    private static final String DATA_PREFIX = "data:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String keyPrefix;
    private final Duration ttl;
    private final HashOperations<String, String, String> hashOperations;

    public RedisStateStorage(
        StringRedisTemplate redisTemplate,
        ObjectMapper objectMapper,
        String keyPrefix,
        Long ttlSeconds
    ) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "redisTemplate must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.keyPrefix = StringUtils.hasText(keyPrefix) ? keyPrefix.trim() : "telegram:fsm";
        this.ttl = ttlSeconds == null ? null : Duration.ofSeconds(Math.max(0L, ttlSeconds));
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void setState(StateKey key, State state) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(state, "state must not be null");
        String redisKey = redisKey(key);
        hashOperations.put(redisKey, STATE_FIELD, state.value());
        touchTtl(redisKey);
    }

    @Override
    public Optional<State> getState(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        String raw = hashOperations.get(redisKey(key), STATE_FIELD);
        if (!StringUtils.hasText(raw)) {
            return Optional.empty();
        }
        return Optional.of(State.of(raw));
    }

    @Override
    public void clearState(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        String redisKey = redisKey(key);
        hashOperations.delete(redisKey, STATE_FIELD);
        cleanupIfEmpty(redisKey);
    }

    @Override
    public void putData(StateKey key, String dataKey, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        String actualDataKey = normalizeDataKey(dataKey);
        String redisKey = redisKey(key);
        String field = DATA_PREFIX + actualDataKey;
        if (value == null) {
            hashOperations.delete(redisKey, field);
            cleanupIfEmpty(redisKey);
            return;
        }
        hashOperations.put(redisKey, field, serializeValue(value));
        touchTtl(redisKey);
    }

    @Override
    public Optional<Object> getData(StateKey key, String dataKey) {
        Objects.requireNonNull(key, "key must not be null");
        String actualDataKey = normalizeDataKey(dataKey);
        String raw = hashOperations.get(redisKey(key), DATA_PREFIX + actualDataKey);
        if (!StringUtils.hasText(raw)) {
            return Optional.empty();
        }
        return Optional.ofNullable(deserializeValue(raw));
    }

    @Override
    public Map<String, Object> getData(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        Map<String, String> entries = hashOperations.entries(redisKey(key));
        if (entries.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String field = entry.getKey();
            if (!field.startsWith(DATA_PREFIX)) {
                continue;
            }
            String dataKey = field.substring(DATA_PREFIX.length());
            result.put(dataKey, deserializeValue(entry.getValue()));
        }
        return Map.copyOf(result);
    }

    @Override
    public void clearData(StateKey key) {
        Objects.requireNonNull(key, "key must not be null");
        String redisKey = redisKey(key);
        Map<String, String> entries = hashOperations.entries(redisKey);
        if (entries.isEmpty()) {
            return;
        }
        var fieldsToRemove = entries.keySet().stream()
            .filter(field -> field.startsWith(DATA_PREFIX))
            .collect(Collectors.toList());
        if (!fieldsToRemove.isEmpty()) {
            hashOperations.delete(redisKey, fieldsToRemove.toArray());
        }
        cleanupIfEmpty(redisKey);
    }

    private String serializeValue(Object value) {
        try {
            String payload = objectMapper.writeValueAsString(value);
            StoredValue stored = new StoredValue(value.getClass().getName(), payload);
            return objectMapper.writeValueAsString(stored);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize FSM state data value", e);
        }
    }

    private Object deserializeValue(String raw) {
        try {
            StoredValue stored = objectMapper.readValue(raw, StoredValue.class);
            if (stored == null || !StringUtils.hasText(stored.payload())) {
                return null;
            }
            Class<?> type = resolveClass(stored.type());
            if (type != null) {
                return objectMapper.readValue(stored.payload(), type);
            }
            return objectMapper.readValue(stored.payload(), Object.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize FSM state data value", ex);
        }
    }

    private Class<?> resolveClass(String className) {
        if (!StringUtils.hasText(className)) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String redisKey(StateKey key) {
        return keyPrefix + ":" + sanitize(key.botId()) + ":" + key.chatId() + ":" + key.userId();
    }

    private String sanitize(String value) {
        return value.replace(':', '_');
    }

    private String normalizeDataKey(String dataKey) {
        Objects.requireNonNull(dataKey, "dataKey must not be null");
        if (dataKey.isBlank()) {
            throw new IllegalArgumentException("dataKey must not be blank");
        }
        return dataKey;
    }

    private void touchTtl(String redisKey) {
        if (ttl == null) {
            return;
        }
        if (ttl.isZero() || ttl.isNegative()) {
            return;
        }
        redisTemplate.expire(redisKey, ttl);
    }

    private void cleanupIfEmpty(String redisKey) {
        Long size = hashOperations.size(redisKey);
        if (size != null && size == 0L) {
            redisTemplate.delete(redisKey);
        }
    }

    private record StoredValue(
        String type,
        String payload
    ) {
    }
}
