package ru.tardyon.botframework.telegram.api.model.checklist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChecklistTask(
    Integer id,
    String text,
    @JsonProperty("text_entities") List<MessageEntity> textEntities,
    @JsonProperty("completed_by_user") User completedByUser,
    @JsonProperty("completed_by_chat") Chat completedByChat,
    @JsonProperty("completion_date") Integer completionDate
) {

    public ChecklistTask {
        textEntities = textEntities == null ? null : List.copyOf(textEntities);
    }
}
