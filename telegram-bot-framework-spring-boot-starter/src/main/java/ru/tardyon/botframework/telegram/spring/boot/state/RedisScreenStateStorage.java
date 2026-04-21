package ru.tardyon.botframework.telegram.spring.boot.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.screen.ScreenFrame;
import ru.tardyon.botframework.telegram.screen.ScreenKey;
import ru.tardyon.botframework.telegram.screen.ScreenStack;
import ru.tardyon.botframework.telegram.screen.ScreenStateStorage;

public final class RedisScreenStateStorage implements ScreenStateStorage {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String keyPrefix;
    private final Duration ttl;

    public RedisScreenStateStorage(
        StringRedisTemplate redisTemplate,
        ObjectMapper objectMapper,
        String keyPrefix,
        Long ttlSeconds
    ) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "redisTemplate must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.keyPrefix = StringUtils.hasText(keyPrefix) ? keyPrefix.trim() : "telegram:screen";
        this.ttl = ttlSeconds == null ? null : Duration.ofSeconds(Math.max(0L, ttlSeconds));
    }

    @Override
    public Optional<ScreenStack> find(ScreenKey key) {
        Objects.requireNonNull(key, "key must not be null");
        String raw = redisTemplate.opsForValue().get(redisKey(key));
        if (!StringUtils.hasText(raw)) {
            return Optional.empty();
        }
        return Optional.of(deserialize(raw));
    }

    @Override
    public ScreenStack getOrCreate(ScreenKey key) {
        return find(key).orElseGet(() -> {
            ScreenStack created = new ScreenStack();
            save(key, created);
            return created;
        });
    }

    @Override
    public void save(ScreenKey key, ScreenStack stack) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(stack, "stack must not be null");
        String redisKey = redisKey(key);
        redisTemplate.opsForValue().set(redisKey, serialize(stack));
        touchTtl(redisKey);
    }

    @Override
    public void clear(ScreenKey key) {
        Objects.requireNonNull(key, "key must not be null");
        redisTemplate.delete(redisKey(key));
    }

    private String redisKey(ScreenKey key) {
        return keyPrefix + ":" + sanitize(key.botId()) + ":" + key.chatId();
    }

    private String sanitize(String value) {
        return value.replace(':', '_');
    }

    private void touchTtl(String redisKey) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }
        redisTemplate.expire(redisKey, ttl);
    }

    private String serialize(ScreenStack stack) {
        StoredScreenStack stored = toStored(stack);
        try {
            return objectMapper.writeValueAsString(stored);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize screen stack", e);
        }
    }

    private ScreenStack deserialize(String raw) {
        try {
            StoredScreenStack stored = objectMapper.readValue(raw, StoredScreenStack.class);
            return fromStored(stored);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize screen stack", e);
        }
    }

    private StoredScreenStack toStored(ScreenStack stack) {
        List<StoredFrame> frames = stack.framesSnapshot().stream()
            .map(frame -> new StoredFrame(
                frame.screenId(),
                frame.data().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> serializeValue(e.getValue())))
            ))
            .toList();
        String renderedKind = stack.renderedMessageKind().map(Enum::name).orElse(null);
        Integer renderedMessageId = stack.renderedMessageId().orElse(null);
        return new StoredScreenStack(frames, renderedMessageId, renderedKind);
    }

    private ScreenStack fromStored(StoredScreenStack stored) {
        if (stored == null) {
            return new ScreenStack();
        }
        List<ScreenFrame> frames = stored.frames() == null ? List.of() : stored.frames().stream()
            .filter(Objects::nonNull)
            .map(frame -> {
                ScreenFrame screenFrame = new ScreenFrame(frame.screenId());
                if (frame.data() != null) {
                    frame.data().forEach((k, v) -> screenFrame.putData(k, deserializeValue(v)));
                }
                return screenFrame;
            })
            .toList();

        ScreenStack.RenderedMessageKind renderedKind = null;
        if (StringUtils.hasText(stored.renderedMessageKind())) {
            renderedKind = ScreenStack.RenderedMessageKind.valueOf(stored.renderedMessageKind());
        }
        return ScreenStack.restore(frames, stored.renderedMessageId(), renderedKind);
    }

    private StoredValue serializeValue(Object value) {
        try {
            String payload = objectMapper.writeValueAsString(value);
            return new StoredValue(value.getClass().getName(), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize screen frame data value", e);
        }
    }

    private Object deserializeValue(StoredValue value) {
        if (value == null || !StringUtils.hasText(value.payload())) {
            return null;
        }
        try {
            Class<?> type = resolveClass(value.type());
            if (type != null) {
                return objectMapper.readValue(value.payload(), type);
            }
            return objectMapper.readValue(value.payload(), Object.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize screen frame data value", e);
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

    private record StoredScreenStack(
        List<StoredFrame> frames,
        Integer renderedMessageId,
        String renderedMessageKind
    ) {
    }

    private record StoredFrame(
        String screenId,
        Map<String, StoredValue> data
    ) {
    }

    private record StoredValue(
        String type,
        String payload
    ) {
    }
}
