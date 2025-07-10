package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.auth.configs.JwtUtil;
import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.services.AppUserService;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import com.ak.task_manger_api.tasks.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class TaskControllerTests {
    private static final String tasksEndPoint = "/api/tasks";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    TaskService taskService;
    @MockitoBean
    AppUserService appUserService;
    @MockitoBean
    TaskRepository taskRepository;
    @Autowired
    JwtUtil jwtUtil;


    private AppUser mockUser;
    private String token;

    @BeforeEach
    void setUp() {
        mockUser = AppUser.builder().id(1L).username("User_01").password("Password").role("USER").build();
        token = jwtUtil.generateToken(mockUser);

        User user = new User(mockUser.getUsername(),
                mockUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + mockUser.getRole())));

        when(appUserService.getCurrentUser(new UsernamePasswordAuthenticationToken(mockUser, any()))).thenReturn(mockUser);
        when(appUserService.loadUserByUsername(mockUser.getUsername())).thenReturn(user);
    }

    @Test
    void shouldReturnAllOwnedTasks() throws Exception {
        List<Task> tasks = List.of(
                new Task(1L, "Test 1", "Desc 1", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false),
                new Task(2L, "Test 2", "Desc 2", true, mockUser, LocalDateTime.now(), LocalDateTime.now(), false),
                new Task(3L, "Test 3", "Desc 3", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false)
        );

        Page<Task> taskPage = new PageImpl<>(tasks);

        when(taskService.getAllOwnedTasks(mockUser, 0, 10)).thenReturn(taskPage);

        mockMvc.perform(get(tasksEndPoint)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(6))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.numberOfElements").value(3))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content.size()").value(3))
                .andExpect(jsonPath("$.data.content[0].title").value("Test 1"))
                .andExpect(jsonPath("$.data.content[1].completed").value(true));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(0, mockUser);

        mockMvc.perform(delete(tasksEndPoint + "/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Task deleted successfully"));
    }

    @Test
    void shouldRestoreTask() throws Exception {
        Task task = Task.builder()
                .id(1L)
                .title("Deleted Task")
                .description("Some desc")
                .completed(false)
                .deleted(true)
                .user(mockUser)
                .build();

        when(taskService.restoreTask(1L, mockUser)).thenReturn(task);

        taskService.restoreTask(1L, mockUser);

        mockMvc.perform(patch(tasksEndPoint + "/1/restore")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Task restored successfully"));
    }

    @Nested
    class CreateTaskTests {
        @Test
        void shouldReturnValidationError() throws Exception {
            TaskRequest taskRequest = new TaskRequest("", "", null);

            mockMvc.perform(post(tasksEndPoint)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details[0].field").exists())
                    .andExpect(jsonPath("$.details[0].error").exists());
            ;
        }

        @Test
        void shouldCreateTask() throws Exception {
            TaskRequest taskRequest = new TaskRequest("title", "desc", false);

            Task createdTask = new Task(1L, taskRequest.getTitle(), taskRequest.getDescription(), taskRequest.getCompleted(), mockUser, LocalDateTime.now(), LocalDateTime.now(), false);

            when(taskService.createTask(taskRequest, mockUser)).thenReturn(createdTask);

            mockMvc.perform(post(tasksEndPoint)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(1L));
        }
    }

    @Nested
    class GetTaskByIdTest {
        @Test
        void shouldReturnTaskIfExistsAndOwned() throws Exception {
            Task task = new Task(1L, "title", "desc", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false);

            when(taskService.getTaskById(1L, mockUser)).thenReturn(task);

            mockMvc.perform(get(tasksEndPoint + "/1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value("1"))
                    .andExpect(jsonPath("$.data.title").value("title"))
                    .andExpect(jsonPath("$.data.description").value("desc"))
                    .andExpect(jsonPath("$.data.completed").value(false));
        }

        @Test
        void shouldReturnNotFoundIfNotExists() throws Exception {

            when(taskService.getTaskById(1000L, mockUser)).thenThrow(EntityNotFoundException.class);

            mockMvc.perform(get(tasksEndPoint + "/1000")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateTaskTest {
        @Test
        void shouldUpdateTaskIfExists() throws Exception {
            Task task = new Task(1L, "title", "desc", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false);

            TaskRequest taskRequest = new TaskRequest("updated title", "updated desc", true);

            Task updatedTask = Task.fromDTO(taskRequest);

            when(taskService.updateTask(task.getId(), taskRequest, mockUser)).thenAnswer(invocation -> {
                updatedTask.setId(task.getId());
                updatedTask.setUser(mockUser);
                return updatedTask;
            });

            mockMvc.perform(patch(tasksEndPoint + "/" + task.getId())
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTask)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("updated title"))
                    .andExpect(jsonPath("$.data.description").value("updated desc"))
                    .andExpect(jsonPath("$.data.completed").value(true));
        }

        @Test
        void shouldThrowEntityNotFoundExceptionIfNotExists() throws Exception {
            TaskRequest taskRequest = new TaskRequest("updated title", "updated desc", true);

            when(taskService.updateTask(1000, taskRequest, mockUser))
                    .thenThrow(EntityNotFoundException.class);

            mockMvc.perform(patch(tasksEndPoint + "/1000")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskRequest)))
                    .andExpect(status().isNotFound());
        }
    }
}
