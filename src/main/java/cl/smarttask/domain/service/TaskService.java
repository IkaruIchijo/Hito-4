package cl.smarttask.domain.service;

import java.util.List;

import cl.smarttask.domain.exception.DuplicateTaskException;
import cl.smarttask.domain.exception.InvalidTaskException;
import cl.smarttask.domain.exception.TaskNotFoundException;
import cl.smarttask.domain.model.NormalTask;
import cl.smarttask.domain.model.Priority;
import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.model.UrgentTask;
import cl.smarttask.domain.repository.TaskRepository;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(int id, String title, Priority priority) {

        validateTask(id, title, priority);

        if (taskRepository.existsById(id)) {
            throw new DuplicateTaskException(id);
        }

        Task task;

        if (priority == Priority.HIGH) {
            task = new UrgentTask(id, title.trim(), priority);
        } else {
            task = new NormalTask(id, title.trim(), priority);
        }

        taskRepository.save(task);

        return task;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(int id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task completeTask(int id) {

        Task task = getTaskById(id);

        task.complete();

        taskRepository.save(task);

        return task;
    }

    public void deleteTask(int id) {

        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }

        taskRepository.deleteById(id);
    }

    private void validateTask(int id, String title, Priority priority) {

        if (id <= 0) {
            throw new InvalidTaskException(
                "Task id must be greater than zero"
            );
        }

        if (title == null || title.isBlank()) {
            throw new InvalidTaskException(
                "Task title cannot be blank"
            );
        }

        if (priority == null) {
            throw new InvalidTaskException(
                "Task priority cannot be null"
            );
        }
    }
}
