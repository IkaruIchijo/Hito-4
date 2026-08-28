package cl.smarttask.infrastructure.web.mapper;

import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.model.UrgentTask;
import cl.smarttask.infrastructure.web.dto.TaskResponse;

public final class TaskWebMapper {

    private TaskWebMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        String type = task instanceof UrgentTask ? "URGENT" : "NORMAL";

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                type,
                task.isCompleted()
        );
    }
}
