package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.ReplyParameters;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendAudioRequest(@JsonProperty("chat_id") Object chatId, @JsonProperty("business_connection_id") String businessConnectionId, InputFile audio, String caption, @JsonProperty("reply_markup") ReplyMarkup replyMarkup, @JsonProperty("reply_parameters") ReplyParameters replyParameters) {
    public SendAudioRequest { Objects.requireNonNull(chatId, "chatId must not be null"); Objects.requireNonNull(audio, "audio must not be null"); }
    public static SendAudioRequest of(long chatId, InputFile audio) { return new SendAudioRequest(chatId, null, audio, null, null, null); }
    public SendAudioRequest(Object chatId, String businessConnectionId, InputFile audio, String caption, ReplyMarkup replyMarkup) { this(chatId, businessConnectionId, audio, caption, replyMarkup, null); }
    public SendAudioRequest withReplyTo(int messageId) { return new SendAudioRequest(chatId, businessConnectionId, audio, caption, replyMarkup, ReplyParameters.of(messageId)); }
}
