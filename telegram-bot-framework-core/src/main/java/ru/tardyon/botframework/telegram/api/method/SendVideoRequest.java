package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendVideoRequest(@JsonProperty("chat_id") Object chatId, @JsonProperty("business_connection_id") String businessConnectionId, InputFile video, String caption, @JsonProperty("reply_markup") ReplyMarkup replyMarkup) {
    public SendVideoRequest { Objects.requireNonNull(chatId, "chatId must not be null"); Objects.requireNonNull(video, "video must not be null"); }
    public static SendVideoRequest of(long chatId, InputFile video) { return new SendVideoRequest(chatId, null, video, null, null); }
}
