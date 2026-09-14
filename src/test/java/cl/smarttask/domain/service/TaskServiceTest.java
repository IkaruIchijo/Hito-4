package cl.smarttask.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import cl.smarttask.domain.exception.DuplicateTaskException;
import cl.smarttask.domain.exception.InvalidTaskException;
import cl.smarttask.domain.exception.TaskNotFoundException;
import cl.smarttask.domain.model.NormalTask;
import cl.smarttask.domain.model.Priority;
import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.model.UrgentTask;
import cl.smarttask.domain.repository.TaskRepository;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {

        taskRepository = Mockito.mock(TaskRepository.class);

        taskService = new TaskService(
            taskRepository
        );
    }

    @Test
    void shouldCreateNormalTask() {

        // Arrange
        when(taskRepository.existsById(1))
            .thenReturn(false);

        // Act
        Task task = taskService.createTask(
            1,
            "Study Java",
            Priority.MEDIUM
        );

        // Assert
        assertEquals(1, task.getId());
        assertEquals("Study Java", task.getTitle());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertInstanceOf(NormalTask.class, task);

        verify(taskRepository).save(task);
    }

    @Test
    void shouldCreateUrgentTask() {

        // Arrange
        when(taskRepository.existsById(2))
            .thenReturn(false);

        // Act
        Task task = taskService.createTask(
            2,
            "Finish project",
            Priority.HIGH
        );

        // Assert
        assertEquals(2, task.getId());
        assertEquals("Finish project", task.getTitle());
        assertEquals(Priority.HIGH, task.getPriority());
        assertInstanceOf(UrgentTask.class, task);

        verify(taskRepository).save(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskIdIsDuplicated() {

        // Arrange
        when(taskRepository.existsById(1))
            .thenReturn(true);

        // Act + Assert
        assertThrows(
            DuplicateTaskException.class,
            () -> taskService.createTask(
                1,
                "Study Java",
                Priority.MEDIUM
            )
        );

        verify(taskRepository, never())
            .save(Mockito.any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenIdIsInvalid() {

        // Arrange
        int invalidId = 0;

        // Act + Assert
        assertThrows(
            InvalidTaskException.class,
            () -> taskService.createTask(
                invalidId,
                "Study Java",
                Priority.MEDIUM
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {

        // Arrange
        String blankTitle = "   ";

        // Act + Assert
        assertThrows(
            InvalidTaskException.class,
            () -> taskService.createTask(
                1,
                blankTitle,
                Priority.MEDIUM
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {

        // Arrange
        String invalidTitle = null;

        // Act + Assert
        assertThrows(
            InvalidTaskException.class,
            () -> taskService.createTask(
                1,
                invalidTitle,
                Priority.MEDIUM
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenPriorityIsNull() {

        // Arrange
        Priority invalidPriority = null;

        // Act + Assert
        assertThrows(
            InvalidTaskException.class,
            () -> taskService.createTask(
                1,
                "Study Java",
                invalidPriority
            )
        );
    }

    @Test
    void shouldReturnTaskWhenIdExists() {

        // Arrange
        Task task = new NormalTask(
            1,
            "Study Java",
            Priority.MEDIUM
        );

        when(taskRepository.findById(1))
            .thenReturn(Optional.of(task));

        // Act
        Task result = taskService.getTaskById(1);

        // Assert
        assertSame(task, result);

        verify(taskRepository).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {

        // Arrange
        when(taskRepository.findById(99))
            .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
            TaskNotFoundException.class,
            () -> taskService.getTaskById(99)
        );

        verify(taskRepository).findById(99);
    }

    @Test
    void shouldCompleteTask() {

        // Arrange
        Task task = new NormalTask(
            1,
            "Study Java",
            Priority.MEDIUM
        );

        when(taskRepository.findById(1))
            .thenReturn(Optional.of(task));

        // Act
        Task result = taskService.completeTask(1);

        // Assert
        assertTrue(task.isCompleted());
        assertSame(task, result);

        verify(taskRepository).findById(1);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldReturnAllTasks() {

        // Arrange
        Task task1 = new NormalTask(
            1,
            "Study Java",
            Priority.MEDIUM
        );

        Task task2 = new UrgentTask(
            2,
            "Finish project",
            Priority.HIGH
        );

        List<Task> tasks = List.of(
            task1,
            task2
        );

        when(taskRepository.findAll())
            .thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        assertSame(tasks, result);

        verify(taskRepository).findAll();
    }

    @Test
    void shouldDeleteTaskWhenIdExists() {

        // Arrange
        when(taskRepository.existsById(1))
            .thenReturn(true);

        // Act
        taskService.deleteTask(1);

        // Assert
        verify(taskRepository).existsById(1);
        verify(taskRepository).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingTask() {

        // Arrange
        when(taskRepository.existsById(99))
            .thenReturn(false);

        // Act + Assert
        assertThrows(
            TaskNotFoundException.class,
            () -> taskService.deleteTask(99)
        );

        verify(taskRepository).existsById(99);
        verify(taskRepository, never()).deleteById(99);
    }

}
