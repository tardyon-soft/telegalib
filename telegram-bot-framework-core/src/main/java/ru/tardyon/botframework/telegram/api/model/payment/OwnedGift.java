package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = OwnedGiftRegular.class, name = "regular"),
        @JsonSubTypes.Type(value = OwnedGiftUnique.class, name = "unique")
    }
)
public sealed interface OwnedGift permits OwnedGiftRegular, OwnedGiftUnique {

    String type();
}
