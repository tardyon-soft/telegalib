package ru.tardyon.botframework.telegram.spring.boot.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportMode;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportProfile;

@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotFrameworkProperties {

    private String token;
    private Mode mode = Mode.POLLING;
    private final Polling polling = new Polling();
    private final Webhook webhook = new Webhook();
    private final WebApp webApp = new WebApp();
    private final Transport transport = new Transport();
    private final Diagnostics diagnostics = new Diagnostics();
    private final StateSettings state = new StateSettings();
    private final ScreenStateSettings screenState = new ScreenStateSettings();

    public enum Mode {
        POLLING,
        WEBHOOK
    }

    public enum StateStorageType {
        MEMORY,
        REDIS
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Polling getPolling() {
        return polling;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public WebApp getWebApp() {
        return webApp;
    }

    public Transport getTransport() {
        return transport;
    }

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    public StateSettings getState() {
        return state;
    }

    public ScreenStateSettings getScreenState() {
        return screenState;
    }

    public boolean isWebhookMode() {
        return mode == Mode.WEBHOOK && webhook.isEnabled();
    }

    public boolean isPollingMode() {
        return mode == Mode.POLLING && polling.isEnabled();
    }

    public String resolveWebhookUrl() {
        if (!StringUtils.hasText(webhook.getPublicUrl())) {
            return null;
        }
        String base = webhook.getPublicUrl().trim();
        String path = webhook.getPath();
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            return trimRightSlash(base);
        }
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        if (base.endsWith(normalizedPath)) {
            return base;
        }
        return trimRightSlash(base) + normalizedPath;
    }

    private static String trimRightSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    public static class Polling {

        private boolean enabled = true;
        private int timeout = 30;
        private int limit = 100;
        private List<String> allowedUpdates;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public List<String> getAllowedUpdates() {
            return allowedUpdates;
        }

        public void setAllowedUpdates(List<String> allowedUpdates) {
            this.allowedUpdates = allowedUpdates;
        }
    }

    public static class Webhook {

        private boolean enabled;
        private String path = "/telegram/webhook";
        private String publicUrl;
        private String secretToken;
        private Boolean dropPendingUpdates;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPublicUrl() {
            return publicUrl;
        }

        public void setPublicUrl(String publicUrl) {
            this.publicUrl = publicUrl;
        }

        public String getSecretToken() {
            return secretToken;
        }

        public void setSecretToken(String secretToken) {
            this.secretToken = secretToken;
        }

        public Boolean getDropPendingUpdates() {
            return dropPendingUpdates;
        }

        public void setDropPendingUpdates(Boolean dropPendingUpdates) {
            this.dropPendingUpdates = dropPendingUpdates;
        }
    }

    public static class WebApp {

        private Long initDataMaxAgeSeconds;

        public Long getInitDataMaxAgeSeconds() {
            return initDataMaxAgeSeconds;
        }

        public void setInitDataMaxAgeSeconds(Long initDataMaxAgeSeconds) {
            this.initDataMaxAgeSeconds = initDataMaxAgeSeconds;
        }
    }

    public static class Transport {

        private BotApiTransportMode mode = BotApiTransportMode.CLOUD;
        private String baseUrl;
        private boolean localFileUriUploadEnabled = true;

        public BotApiTransportMode getMode() {
            return mode;
        }

        public void setMode(BotApiTransportMode mode) {
            this.mode = mode;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public boolean isLocalFileUriUploadEnabled() {
            return localFileUriUploadEnabled;
        }

        public void setLocalFileUriUploadEnabled(boolean localFileUriUploadEnabled) {
            this.localFileUriUploadEnabled = localFileUriUploadEnabled;
        }

        public String resolveBaseUrl() {
            if (StringUtils.hasText(baseUrl)) {
                return baseUrl.trim();
            }
            if (mode == BotApiTransportMode.LOCAL) {
                return "http://127.0.0.1:8081";
            }
            return BotApiTransportProfile.DEFAULT_CLOUD_BASE_URL;
        }
    }

    public static class Diagnostics {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class StateSettings {

        private StateStorageType storage = StateStorageType.MEMORY;
        private final Redis redis = new Redis();

        public StateStorageType getStorage() {
            return storage;
        }

        public void setStorage(StateStorageType storage) {
            this.storage = storage;
        }

        public Redis getRedis() {
            return redis;
        }
    }

    public static class ScreenStateSettings {

        private StateStorageType storage = StateStorageType.MEMORY;
        private final ScreenRedis redis = new ScreenRedis();

        public StateStorageType getStorage() {
            return storage;
        }

        public void setStorage(StateStorageType storage) {
            this.storage = storage;
        }

        public ScreenRedis getRedis() {
            return redis;
        }
    }

    public static class Redis {

        private String keyPrefix = "telegram:fsm";
        private Long ttlSeconds;

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public Long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(Long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }

    public static class ScreenRedis {

        private String keyPrefix = "telegram:screen";
        private Long ttlSeconds;

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public Long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(Long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }
}
