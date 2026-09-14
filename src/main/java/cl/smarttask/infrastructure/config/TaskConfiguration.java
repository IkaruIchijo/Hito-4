package cl.smarttask.infrastructure.config;

import cl.smarttask.domain.repository.TaskRepository;
import cl.smarttask.domain.service.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TaskConfiguration {

    @Bean
    TaskService taskService(TaskRepository taskRepository) {
        return new TaskService(taskRepository);
    }
}
