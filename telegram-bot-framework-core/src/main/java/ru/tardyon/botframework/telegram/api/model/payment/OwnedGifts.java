package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnedGifts(
    @JsonProperty("total_count") Integer totalCount,
    List<OwnedGift> gifts,
    @JsonProperty("next_offset") String nextOffset
) {

    public OwnedGifts {
        gifts = gifts == null ? List.of() : List.copyOf(gifts);
    }
}
