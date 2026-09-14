package cl.smarttask.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.smarttask.domain.model.NormalTask;
import cl.smarttask.domain.model.Priority;
import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.model.UrgentTask;
import cl.smarttask.domain.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaTaskRepositoryAdapter.class)
class JpaTaskRepositoryAdapterTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldPersistAndRestoreUrgentCompletedTask() {
        Task task = new UrgentTask(2, "Finish project", Priority.HIGH);
        task.complete();

        taskRepository.save(task);

        Task restored = taskRepository.findById(2).orElseThrow();
        assertEquals(2, restored.getId());
        assertEquals("Finish project", restored.getTitle());
        assertEquals(Priority.HIGH, restored.getPriority());
        assertInstanceOf(UrgentTask.class, restored);
        assertTrue(restored.isCompleted());
    }

    @Test
    void shouldReturnTasksOrderedById() {
        taskRepository.save(new NormalTask(3, "Third", Priority.LOW));
        taskRepository.save(new NormalTask(1, "First", Priority.MEDIUM));

        var tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
        assertEquals(1, tasks.get(0).getId());
        assertEquals(3, tasks.get(1).getId());
    }
}
