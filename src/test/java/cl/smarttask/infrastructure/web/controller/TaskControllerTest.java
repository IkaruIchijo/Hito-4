package cl.smarttask.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import cl.smarttask.domain.exception.DuplicateTaskException;
import cl.smarttask.domain.exception.TaskNotFoundException;
import cl.smarttask.domain.model.NormalTask;
import cl.smarttask.domain.model.Priority;
import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.model.UrgentTask;
import cl.smarttask.domain.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void shouldCreateTaskAndReturn201() throws Exception {
        Task task = new UrgentTask(1, "Complete Hito 4", Priority.HIGH);
        when(taskService.createTask(1, "Complete Hito 4", Priority.HIGH)).thenReturn(task);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "title": "Complete Hito 4",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("URGENT"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void shouldReturnAllTasksAnd200() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(
                new NormalTask(1, "Study", Priority.MEDIUM),
                new UrgentTask(2, "Deliver", Priority.HIGH)
        ));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("NORMAL"))
                .andExpect(jsonPath("$[1].type").value("URGENT"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 0,
                                  "title": " ",
                                  "priority": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/api/v1/tasks"));
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExist() throws Exception {
        when(taskService.getTaskById(99)).thenThrow(new TaskNotFoundException(99));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    @Test
    void shouldReturn422WhenTaskIdIsDuplicated() throws Exception {
        when(taskService.createTask(anyInt(), anyString(), eq(Priority.MEDIUM)))
                .thenThrow(new DuplicateTaskException(1));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "title": "Study",
                                  "priority": "MEDIUM"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("DUPLICATE_TASK"));
    }

    @Test
    void shouldDeleteTaskAndReturn204() throws Exception {
        doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
