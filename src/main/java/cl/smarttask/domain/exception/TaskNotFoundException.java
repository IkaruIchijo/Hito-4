package cl.smarttask.domain.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(int id) {
        super("Task not found with id: " + id);
    }
}