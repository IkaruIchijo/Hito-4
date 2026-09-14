package cl.smarttask.infrastructure.web.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta unificada para errores de la API")
public record ErrorResponse(
        @Schema(example = "2026-08-25T21:30:00Z") Instant timestamp,
        @Schema(example = "404") int status,
        @Schema(example = "TASK_NOT_FOUND") String code,
        @Schema(example = "Task not found with id: 99") String message,
        @Schema(example = "/api/v1/tasks/99") String path
) {
}
