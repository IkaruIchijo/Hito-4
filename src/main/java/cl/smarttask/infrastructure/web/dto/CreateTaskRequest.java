package cl.smarttask.infrastructure.web.dto;

import cl.smarttask.domain.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para crear una tarea")
public record CreateTaskRequest(
        @Schema(description = "Identificador unico de la tarea", example = "1")
        @NotNull(message = "id is required")
        @Positive(message = "id must be greater than zero")
        Integer id,

        @Schema(description = "Titulo de la tarea", example = "Completar Hito 4")
        @NotBlank(message = "title cannot be blank")
        @Size(max = 200, message = "title cannot exceed 200 characters")
        String title,

        @Schema(description = "Prioridad de la tarea", example = "HIGH")
        @NotNull(message = "priority is required")
        Priority priority
) {
}
