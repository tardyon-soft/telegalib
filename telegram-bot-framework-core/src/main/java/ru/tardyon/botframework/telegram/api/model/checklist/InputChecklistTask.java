package ru.tardyon.botframework.telegram.api.model.checklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record InputChecklistTask(
    Integer id,
    String text,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("text_entities") List<MessageEntity> textEntities
) {

    public InputChecklistTask {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("id must be positive");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
        if (text.length() > 100) {
            throw new IllegalArgumentException("text length must be in range 1..100");
        }
        textEntities = textEntities == null ? null : List.copyOf(textEntities);
    }
}
