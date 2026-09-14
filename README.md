# SmartTask — Hito 4

SmartTask es un microservicio REST para crear, consultar, completar y eliminar tareas. El proyecto mantiene el dominio Java desacoplado de la infraestructura mediante una arquitectura **Ports & Adapters**.

## Tecnologías

- Java 17+
- Spring Boot 3.5
- Spring Web y Jakarta Validation
- Spring Data JPA
- PostgreSQL 16
- Docker Compose
- Springdoc OpenAPI y Swagger UI
- JUnit 5, Mockito, MockMvc, H2 y JaCoCo

## Evidencias

### Contratos REST documentados con Swagger

![Endpoints de SmartTask documentados con Swagger](docs/screenshots/swagger-endpoints.png)

### Creación de una tarea

La operación `POST /api/v1/tasks` responde `201 Created` y devuelve la tarea registrada.

![Creación exitosa de una tarea con respuesta 201](docs/screenshots/swagger-post-201.png)

### Pruebas y cobertura

El reporte JaCoCo registra **82 % de cobertura de instrucciones** y **100 % de cobertura de ramas**.

![Reporte de cobertura JaCoCo](docs/screenshots/jacoco-cobertura.png)

## Arquitectura

```text
src/main/java/cl/smarttask
├── domain
│   ├── exception
│   ├── model
│   ├── repository       # Puerto de persistencia
│   └── service          # Casos de uso y reglas de negocio
└── infrastructure
    ├── config           # Inyección de dependencias y OpenAPI
    ├── persistence
    │   ├── adapter      # Implementación del puerto de dominio
    │   ├── entity       # Entidades JPA
    │   ├── mapper
    │   └── repository   # Spring Data JpaRepository
    └── web
        ├── controller   # API REST y manejador global de errores
        ├── dto
        └── mapper
```

Las clases de `domain` no contienen anotaciones de Spring ni JPA. La dependencia apunta desde infraestructura hacia el dominio, manteniendo el enfoque de Ports & Adapters.

## Requisitos

- JDK 17 o superior
- Maven 3.6.3 o superior
- Docker Desktop con Docker Compose

## Ejecución local con perfil de desarrollo

1. Crea el archivo local de variables de PostgreSQL:

   ```bash
   cp .env.example .env
   ```

   En PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Levanta PostgreSQL:

   ```bash
   docker compose up -d
   ```

3. Ejecuta la aplicación con el perfil `dev`:

   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

La base de datos queda disponible en `localhost:5432`. El volumen `smarttask_postgres_data` conserva las tareas aunque el contenedor se reinicie.

## Contratos REST

| Método | Ruta | Resultado esperado |
| --- | --- | --- |
| `POST` | `/api/v1/tasks` | Crea una tarea y responde `201 Created` |
| `GET` | `/api/v1/tasks` | Lista las tareas y responde `200 OK` |
| `GET` | `/api/v1/tasks/{id}` | Busca una tarea o responde `404 Not Found` |
| `PATCH` | `/api/v1/tasks/{id}/completion` | Marca una tarea como completada |
| `DELETE` | `/api/v1/tasks/{id}` | Elimina una tarea y responde `204 No Content` |

Ejemplo de creación:

```bash
curl -i -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"id":1,"title":"Completar Hito 4","priority":"HIGH"}'
```

Las prioridades admitidas son `LOW`, `MEDIUM` y `HIGH`. Una prioridad `HIGH` crea una tarea urgente; las restantes crean tareas normales.

## Manejo global de errores

`GlobalExceptionHandler` intercepta errores de validación y excepciones del dominio. La API utiliza un contrato uniforme sin exponer stacktraces:

```json
{
  "timestamp": "2026-08-25T21:30:00Z",
  "status": 404,
  "code": "TASK_NOT_FOUND",
  "message": "Task not found with id: 99",
  "path": "/api/v1/tasks/99"
}
```

Los códigos principales son:

- `400 Bad Request`: JSON incorrecto o validación de entrada.
- `404 Not Found`: tarea o recurso inexistente.
- `422 Unprocessable Entity`: identificador duplicado o regla de negocio inválida.

## Swagger y OpenAPI

Con el perfil `dev` activo:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/api-docs>

Los controladores incluyen `@Tag`, `@Operation` y `@ApiResponses`; los DTO utilizan `@Schema`, permitiendo probar los contratos con **Try it out**.

El perfil por defecto es `prod`. En ese perfil, `springdoc.api-docs.enabled` y `springdoc.swagger-ui.enabled` permanecen en `false`, evitando exponer la documentación interactiva en producción.

## Pruebas y cobertura

Ejecuta todas las pruebas con:

```bash
mvn clean test
```

Las pruebas cubren el dominio, los casos de uso, el adaptador JPA y los contratos HTTP con MockMvc. Durante las pruebas de persistencia se utiliza H2 en modo compatible con PostgreSQL para no depender de Docker.

El reporte de cobertura queda disponible en:

```text
target/site/jacoco/index.html
```

## Colección Postman

La colección `postman/SmartTask-Hito4.postman_collection.json` contiene las cinco operaciones de la API y utiliza las variables `baseUrl` y `taskId`.

## Ejecución productiva

El perfil `prod` exige credenciales externas y no contiene valores por defecto:

```bash
export DB_URL=jdbc:postgresql://servidor:5432/smarttask_db
export DB_USERNAME=smarttask_user
export DB_PASSWORD=una_clave_segura
java -jar target/smarttask-hito4-1.0.0.jar --spring.profiles.active=prod
```

Para generar el ejecutable:

```bash
mvn clean package
```

Para detener PostgreSQL sin eliminar los datos:

```bash
docker compose down
```

## Autor

Sebastián Fuentes
