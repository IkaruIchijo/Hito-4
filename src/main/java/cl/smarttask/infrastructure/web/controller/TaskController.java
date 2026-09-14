package cl.smarttask.infrastructure.web.controller;

import java.net.URI;
import java.util.List;

import cl.smarttask.domain.model.Task;
import cl.smarttask.domain.service.TaskService;
import cl.smarttask.infrastructure.web.dto.CreateTaskRequest;
import cl.smarttask.infrastructure.web.dto.ErrorResponse;
import cl.smarttask.infrastructure.web.dto.TaskResponse;
import cl.smarttask.infrastructure.web.mapper.TaskWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Validated
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Operaciones para gestionar tareas")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Crear una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Identificador duplicado o regla de negocio invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request.id(), request.title(), request.priority());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(location).body(TaskWebMapper.toResponse(task));
    }

    @GetMapping
    @Operation(summary = "Listar todas las tareas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks()
                .stream()
                .map(TaskWebMapper::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar una tarea por identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable @Positive(message = "id must be greater than zero") int id) {
        return ResponseEntity.ok(TaskWebMapper.toResponse(taskService.getTaskById(id)));
    }

    @PatchMapping("/{id}/completion")
    @Operation(summary = "Marcar una tarea como completada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea completada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable @Positive(message = "id must be greater than zero") int id) {
        return ResponseEntity.ok(TaskWebMapper.toResponse(taskService.completeTask(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea eliminada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteTask(
            @PathVariable @Positive(message = "id must be greater than zero") int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
