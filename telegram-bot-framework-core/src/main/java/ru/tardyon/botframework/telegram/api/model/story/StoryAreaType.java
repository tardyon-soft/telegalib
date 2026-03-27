package ru.tardyon.botframework.telegram.api.model.story;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = StoryAreaTypeUniqueGift.class, name = "unique_gift")
    }
)
public sealed interface StoryAreaType permits StoryAreaTypeUniqueGift {

    String type();
}
