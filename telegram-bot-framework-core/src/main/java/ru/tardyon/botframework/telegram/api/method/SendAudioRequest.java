package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendAudioRequest(@JsonProperty("chat_id") Object chatId, @JsonProperty("business_connection_id") String businessConnectionId, InputFile audio, String caption, @JsonProperty("reply_markup") ReplyMarkup replyMarkup) {
    public SendAudioRequest { Objects.requireNonNull(chatId, "chatId must not be null"); Objects.requireNonNull(audio, "audio must not be null"); }
    public static SendAudioRequest of(long chatId, InputFile audio) { return new SendAudioRequest(chatId, null, audio, null, null); }
}
