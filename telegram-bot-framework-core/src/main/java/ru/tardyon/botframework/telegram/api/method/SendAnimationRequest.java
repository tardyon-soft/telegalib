package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.ReplyParameters;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendAnimationRequest(@JsonProperty("chat_id") Object chatId, @JsonProperty("business_connection_id") String businessConnectionId, InputFile animation, String caption, @JsonProperty("reply_markup") ReplyMarkup replyMarkup, @JsonProperty("reply_parameters") ReplyParameters replyParameters) {
    public SendAnimationRequest { Objects.requireNonNull(chatId, "chatId must not be null"); Objects.requireNonNull(animation, "animation must not be null"); }
    public static SendAnimationRequest of(long chatId, InputFile animation) { return new SendAnimationRequest(chatId, null, animation, null, null, null); }
    public SendAnimationRequest(Object chatId, String businessConnectionId, InputFile animation, String caption, ReplyMarkup replyMarkup) { this(chatId, businessConnectionId, animation, caption, replyMarkup, null); }
    public SendAnimationRequest withReplyTo(int messageId) { return new SendAnimationRequest(chatId, businessConnectionId, animation, caption, replyMarkup, ReplyParameters.of(messageId)); }
}
