package cl.smarttask.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void shouldUpdateTaskTitle() {

        // Arrange
        Task task = new NormalTask(
            1,
            "Study Java",
            Priority.MEDIUM
        );

        // Act
        task.setTitle("Study Mockito");

        // Assert
        assertEquals(
            "Study Mockito",
            task.getTitle()
        );
    }

    @Test
    void shouldUpdateTaskPriority() {

        // Arrange
        Task task = new NormalTask(
            1,
            "Study Java",
            Priority.MEDIUM
        );

        // Act
        task.setPriority(Priority.HIGH);

        // Assert
        assertEquals(
            Priority.HIGH,
            task.getPriority()
        );
    }
}