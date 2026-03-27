package ru.tardyon.botframework.telegram.api.model.story;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StoryAreaPosition(
    @JsonProperty("x_percentage") Float xPercentage,
    @JsonProperty("y_percentage") Float yPercentage,
    @JsonProperty("width_percentage") Float widthPercentage,
    @JsonProperty("height_percentage") Float heightPercentage,
    @JsonProperty("rotation_angle") Float rotationAngle
) {
}
