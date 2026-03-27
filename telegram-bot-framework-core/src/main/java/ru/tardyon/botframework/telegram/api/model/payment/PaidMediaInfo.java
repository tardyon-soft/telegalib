package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaidMediaInfo(
    @JsonProperty("star_count") Integer starCount,
    @JsonProperty("paid_media") List<PaidMedia> paidMedia
) {
}

