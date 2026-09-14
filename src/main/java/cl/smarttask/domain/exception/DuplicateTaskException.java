package cl.smarttask.domain.exception;

public class DuplicateTaskException extends RuntimeException {

    public DuplicateTaskException(int id) {
        super("Task already exists with id: " + id);
    }
}