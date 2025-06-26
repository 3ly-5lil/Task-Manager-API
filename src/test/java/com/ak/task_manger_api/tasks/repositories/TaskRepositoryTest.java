package com.ak.task_manger_api.tasks.repositories;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.repositories.AppUserRepository;
import com.ak.task_manger_api.tasks.models.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Testcontainers
public class TaskRepositoryTest {
    // the warning is baseless the container start at the start of test and close at the end
    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.2")
            .withDatabaseName("TaskManagerTestDb")
            .withUsername("user")
            .withPassword("pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    AppUserRepository appUserRepository;

    @Test
    void shouldSaveAndReturnTaskWithIdNotNull() {
        // Arrange
        AppUser user = AppUser.builder().username("User_01").password("Password").role("USER").build();
        Task task = Task.builder().title("title").description("desc").completed(false).build();
        // Act
        AppUser userResult = appUserRepository.save(user);
        task.setUser(userResult);
        Task taskResult = taskRepository.save(task);
        // Assert
        // ensure that the insertion done the auto increment is successful
        assertNotNull(userResult.getId());
        assertNotNull(taskResult.getId());
        assertThat(taskResult.getId()).isGreaterThan(0);
    }

}
