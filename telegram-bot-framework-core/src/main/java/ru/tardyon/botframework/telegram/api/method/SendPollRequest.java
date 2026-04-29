package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendPollRequest(@JsonProperty("chat_id") Object chatId, String question, List<String> options, @JsonProperty("is_anonymous") Boolean isAnonymous, String type, @JsonProperty("allows_multiple_answers") Boolean allowsMultipleAnswers, @JsonProperty("reply_markup") ReplyMarkup replyMarkup) {
    public SendPollRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(question, "question must not be null");
        Objects.requireNonNull(options, "options must not be null");
        if (options.size() < 2 || options.size() > 10) throw new IllegalArgumentException("options size must be in range 2..10");
        options = List.copyOf(options);
    }
}
