package ru.tardyon.botframework.telegram.api.model.checklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record InputChecklist(
    String title,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("title_entities") List<MessageEntity> titleEntities,
    List<InputChecklistTask> tasks,
    @JsonProperty("others_can_add_tasks") Boolean othersCanAddTasks,
    @JsonProperty("others_can_mark_tasks_as_done") Boolean othersCanMarkTasksAsDone
) {

    public InputChecklist {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("title length must be in range 1..255");
        }
        if (tasks == null || tasks.isEmpty() || tasks.size() > 30) {
            throw new IllegalArgumentException("tasks size must be in range 1..30");
        }
        titleEntities = titleEntities == null ? null : List.copyOf(titleEntities);
        tasks = List.copyOf(tasks);

        Set<Integer> ids = new java.util.HashSet<>();
        for (InputChecklistTask task : tasks) {
            if (!ids.add(task.id())) {
                throw new IllegalArgumentException("task ids must be unique");
            }
        }
    }
}
