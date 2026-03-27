package ru.tardyon.botframework.telegram.api.model.inline;

public sealed interface InlineQueryResult permits
    InlineQueryResultArticle,
    InlineQueryResultPhoto,
    InlineQueryResultCachedPhoto {

    String type();

    String id();
}
