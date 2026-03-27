package ru.tardyon.botframework.telegram.api.model.webapp;

import java.util.Objects;

public record WebAppInfo(String url) {

    public WebAppInfo {
        Objects.requireNonNull(url, "url must not be null");
        if (url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
    }
}
