package ru.tardyon.botframework.telegram.exception;

public class TelegramApiException extends RuntimeException {

    private final Integer errorCode;
    private final String description;
    private final String rawBody;

    public TelegramApiException(Integer errorCode, String description, String rawBody) {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
        this.rawBody = rawBody;
    }

    public TelegramApiException(Integer errorCode, String description, String rawBody, Throwable cause) {
        super(description, cause);
        this.errorCode = errorCode;
        this.description = description;
        this.rawBody = rawBody;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public String getRawBody() {
        return rawBody;
    }
}
