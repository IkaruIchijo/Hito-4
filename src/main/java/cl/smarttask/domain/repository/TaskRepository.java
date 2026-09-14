package cl.smarttask.domain.repository;

import java.util.List;
import java.util.Optional;

import cl.smarttask.domain.model.Task;

public interface TaskRepository {

    void save(Task task);

    Optional<Task> findById(int id);

    List<Task> findAll();

    void deleteById(int id);

    boolean existsById(int id);
}