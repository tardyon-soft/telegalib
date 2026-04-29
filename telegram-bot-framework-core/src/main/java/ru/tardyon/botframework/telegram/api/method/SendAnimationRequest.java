package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendAnimationRequest(@JsonProperty("chat_id") Object chatId, @JsonProperty("business_connection_id") String businessConnectionId, InputFile animation, String caption, @JsonProperty("reply_markup") ReplyMarkup replyMarkup) {
    public SendAnimationRequest { Objects.requireNonNull(chatId, "chatId must not be null"); Objects.requireNonNull(animation, "animation must not be null"); }
    public static SendAnimationRequest of(long chatId, InputFile animation) { return new SendAnimationRequest(chatId, null, animation, null, null); }
}
