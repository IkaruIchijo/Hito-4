package cl.smarttask.infrastructure.web.dto;

import cl.smarttask.domain.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representacion publica de una tarea")
public record TaskResponse(
        @Schema(example = "1") int id,
        @Schema(example = "Completar Hito 4") String title,
        @Schema(example = "HIGH") Priority priority,
        @Schema(example = "URGENT") String type,
        @Schema(example = "false") boolean completed
) {
}
