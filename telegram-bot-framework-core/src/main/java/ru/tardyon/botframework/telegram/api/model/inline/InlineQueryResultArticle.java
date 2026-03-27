package ru.tardyon.botframework.telegram.api.model.inline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record InlineQueryResultArticle(
    String type,
    String id,
    String title,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    String url,
    @JsonProperty("hide_url") Boolean hideUrl,
    String description,
    @JsonProperty("thumbnail_url") String thumbnailUrl,
    @JsonProperty("thumbnail_width") Integer thumbnailWidth,
    @JsonProperty("thumbnail_height") Integer thumbnailHeight
) implements InlineQueryResult {

    public InlineQueryResultArticle {
        requireType(type);
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(inputMessageContent, "inputMessageContent must not be null");
    }

    public InlineQueryResultArticle(String id, String title, InputMessageContent inputMessageContent) {
        this("article", id, title, inputMessageContent, null, null, null, null, null, null, null);
    }

    private static void requireType(String type) {
        if (!"article".equals(type)) {
            throw new IllegalArgumentException("type must be 'article'");
        }
    }
}
