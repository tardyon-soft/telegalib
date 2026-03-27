package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = PaidMediaPreview.class, name = "preview"),
        @JsonSubTypes.Type(value = PaidMediaPhoto.class, name = "photo"),
        @JsonSubTypes.Type(value = PaidMediaVideo.class, name = "video")
    }
)
public sealed interface PaidMedia permits PaidMediaPreview, PaidMediaPhoto, PaidMediaVideo {

    String type();
}

