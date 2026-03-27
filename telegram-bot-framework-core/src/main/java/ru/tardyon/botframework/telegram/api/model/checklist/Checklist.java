package ru.tardyon.botframework.telegram.api.model.checklist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Checklist(
    String title,
    @JsonProperty("title_entities") List<MessageEntity> titleEntities,
    List<ChecklistTask> tasks,
    @JsonProperty("others_can_add_tasks") Boolean othersCanAddTasks,
    @JsonProperty("others_can_mark_tasks_as_done") Boolean othersCanMarkTasksAsDone
) {

    public Checklist {
        titleEntities = titleEntities == null ? null : List.copyOf(titleEntities);
        tasks = tasks == null ? List.of() : List.copyOf(tasks);
    }
}
