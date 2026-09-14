package cl.smarttask.infrastructure.persistence.mapper;

import cl.smarttask.domain.model.NormalTask;
import cl.smarttask.domain.model.Priority;
import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.model.UrgentTask;
import cl.smarttask.infrastructure.persistence.entity.TaskEntity;

public final class TaskPersistenceMapper {

    private TaskPersistenceMapper() {
    }

    public static TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.isCompleted()
        );
    }

    public static Task toDomain(TaskEntity entity) {
        Task task = entity.getPriority() == Priority.HIGH
                ? new UrgentTask(entity.getId(), entity.getTitle(), entity.getPriority())
                : new NormalTask(entity.getId(), entity.getTitle(), entity.getPriority());

        if (entity.isCompleted()) {
            task.complete();
        }

        return task;
    }
}
