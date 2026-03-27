package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.media.InputMedia;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaAudio;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaDocument;

public record SendMediaGroupRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("business_connection_id") String businessConnectionId,
    List<InputMedia> media
) {

    public SendMediaGroupRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(media, "media must not be null");
        if (media.size() < 2 || media.size() > 10) {
            throw new IllegalArgumentException("media size must be between 2 and 10");
        }
        media = List.copyOf(media);
        validateAlbumTypeComposition(media);
    }

    public static SendMediaGroupRequest of(long chatId, List<InputMedia> media) {
        return new SendMediaGroupRequest(chatId, null, media);
    }

    public static SendMediaGroupRequest of(String chatId, List<InputMedia> media) {
        return new SendMediaGroupRequest(chatId, null, media);
    }

    public SendMediaGroupRequest(Object chatId, List<InputMedia> media) {
        this(chatId, null, media);
    }

    private static void validateAlbumTypeComposition(List<InputMedia> media) {
        boolean hasDocument = media.stream().anyMatch(InputMediaDocument.class::isInstance);
        boolean hasAudio = media.stream().anyMatch(InputMediaAudio.class::isInstance);

        if (hasDocument && media.stream().anyMatch(item -> !(item instanceof InputMediaDocument))) {
            throw new IllegalArgumentException("Document albums may contain only InputMediaDocument items");
        }
        if (hasAudio && media.stream().anyMatch(item -> !(item instanceof InputMediaAudio))) {
            throw new IllegalArgumentException("Audio albums may contain only InputMediaAudio items");
        }
    }
}
