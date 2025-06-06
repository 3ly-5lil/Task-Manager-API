package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.tasks.controllers.TaskController;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TaskControllerTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    TaskService taskService;

    @Test
    void shouldReturnAllTasks() throws Exception {
        List<Task> mockTasks = List.of(
                new Task(1, "Test 1", "Desc 1", false),
                new Task(2, "Test 2", "Desc 2", true),
                new Task(3, "Test 3", "Desc 3", false)
        );

        when(taskService.getAllTasks())
                .thenReturn(mockTasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].title").value("Test 1"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    @Nested
    class GetTaskByIdTest {
        @Test
        void shouldReturnTaskIfExists() throws Exception {
            Task task = new Task(1, "title", "desc", false);

            when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

            mockMvc.perform(get("/tasks/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.title").value("title"))
                    .andExpect(jsonPath("$.description").value("desc"))
                    .andExpect(jsonPath("$.completed").value(false));
        }

        @Test
        void shouldReturnNotFoundIfNotExists() throws Exception {

            when(taskService.getTaskById(1000)).thenReturn(Optional.empty());

            mockMvc.perform(get("/tasks/1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void shouldSaveTask() throws Exception {
        Task task = Task.builder()
                .title("title")
                .description("desc")
                .completed(false)
                .build();

        Task createdTask = new Task(1, task.getTitle(), task.getDescription(), task.isCompleted());

        when(taskService.createTask(task))
                .thenReturn(createdTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Nested
    class UpdateTaskTest {
        @Test
        void shouldUpdateTaskIfExists() throws Exception {
            Task task = new Task(1, "title", "desc", false);
            Task updatedTask = new Task(null, "updated title", "updated desc", true);

            when(taskService.updateTask(task.getId(), updatedTask)).thenAnswer(invocation -> {
                updatedTask.setId(task.getId());
                return updatedTask;
            });

            mockMvc.perform(put("/tasks/" + task.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTask)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("updated title"))
                    .andExpect(jsonPath("$.description").value("updated desc"))
                    .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void shouldThrowExceptionIfNotExists() throws Exception {
            Task updatedTask = new Task(null, "updated title", "updated desc", true);

            when(taskService.updateTask(eq(1000), any())).thenThrow(new RuntimeException("Task not found"));

            mockMvc.perform(put("/tasks/1000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTask)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Task not found"));
        }
    }

    @Test
    void shouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(0);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
