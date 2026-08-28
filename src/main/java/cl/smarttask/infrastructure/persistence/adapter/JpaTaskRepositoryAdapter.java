package cl.smarttask.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.repository.TaskRepository;
import cl.smarttask.infrastructure.persistence.mapper.TaskPersistenceMapper;
import cl.smarttask.infrastructure.persistence.repository.TaskJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class JpaTaskRepositoryAdapter implements TaskRepository {

    private final TaskJpaRepository taskJpaRepository;

    public JpaTaskRepositoryAdapter(TaskJpaRepository taskJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
    }

    @Override
    public void save(Task task) {
        taskJpaRepository.save(TaskPersistenceMapper.toEntity(task));
    }

    @Override
    public Optional<Task> findById(int id) {
        return taskJpaRepository.findById(id)
                .map(TaskPersistenceMapper::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return taskJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(TaskPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(int id) {
        taskJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(int id) {
        return taskJpaRepository.existsById(id);
    }
}
