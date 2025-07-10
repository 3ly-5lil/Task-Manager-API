package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    private AppUser mockUser;

    @BeforeEach
    void setUp() {
        mockUser = AppUser.builder().id(1L).username("User_01").password("Password").role("USER").build();
    }

    @Test
    void shouldReturnAllTask() {
        List<Task> mockTasks = List.of(
                new Task(1L, "Test 1", "Desc 1", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false),
                new Task(2L, "Test 2", "Desc 2", true, mockUser, LocalDateTime.now(), LocalDateTime.now(), false),
                new Task(3L, "Test 3", "Desc 3", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false),
                new Task(3L, "Test 3", "Desc 3", false, new AppUser(0L, null, null, null, LocalDateTime.now(), LocalDateTime.now()), LocalDateTime.now(), LocalDateTime.now(), false)
        );

        when(taskRepository.findByUserAndDeletedFalse(eq(mockUser), any())).thenReturn(new PageImpl<>(mockTasks.subList(0, 3)));

        Page<Task> result = taskService.getAllOwnedTasks(mockUser, 0, 10);

        assertEquals(mockTasks.size() - 1, result.getTotalElements());
        verify(taskRepository).findByUserAndDeletedFalse(eq(mockUser), any());
    }

    @Test
    void shouldCreateAndReturnTask() {
        TaskRequest taskRequest = new TaskRequest("Task 5", "Desc 5", false);
        Task task = Task.fromDTO(taskRequest);
        task.setUser(mockUser);

        Task createdTask = new Task(1L, task.getTitle(), task.getDescription(), task.getCompleted(), mockUser, LocalDateTime.now(), LocalDateTime.now(), false);

        when(taskRepository.save(task)).thenReturn(createdTask);

        Task result = taskService.createTask(taskRequest, mockUser);

        assertNotNull(result.getId());
        assertEquals(result.getId(), createdTask.getId());

        verify(taskRepository).save(task);
    }

    @Test
    void shouldRestoreTask() {
        Task task = Task.builder()
                .id(1L)
                .title("Deleted Task")
                .description("Some desc")
                .completed(false)
                .deleted(true)
                .user(mockUser)
                .build();

        when(taskRepository.findByIdAndUserAndDeletedTrue(1L, mockUser))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);

        Task restored = taskService.restoreTask(1L, mockUser);

        assertFalse(restored.getDeleted());
        verify(taskRepository).save(task);

    }

    @Nested
    class DeleteTaskTests {
        @Test
        void shouldDeleteTaskWhenExistAndOwned() {
            Task mockedTask = new Task(1L, "Task", "Desc", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false);

            when(taskRepository.findByIdAndUserAndDeletedFalse(mockedTask.getId(), mockUser)).thenReturn(Optional.of(mockedTask));

            taskService.deleteTask(mockedTask.getId(), mockUser);

            assertTrue(mockedTask.getDeleted());
            verify(taskRepository).save(mockedTask);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenTaskNotExists() {
            Task mockedTask = new Task(1000L, "Task", "Desc", false, null, LocalDateTime.now(), LocalDateTime.now(), false);

            when(taskRepository.findByIdAndUserAndDeletedFalse(mockedTask.getId(), mockUser)).thenThrow(EntityNotFoundException.class);

            assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(mockedTask.getId(), mockUser));

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    class GetTaskByIdTests {
        @Test
        void shouldReturnTaskWhenExists() {
            Task task = new Task(1L, "title", "desc", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false);
            when(taskRepository.findByIdAndUserAndDeletedFalse(1L, mockUser)).thenReturn(Optional.of(task));

            Task result = taskService.getTaskById(1L, mockUser);

            assertEquals(task, result);
            verify(taskRepository).findByIdAndUserAndDeletedFalse(1L, mockUser);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenNotExists() {
            when(taskRepository.findByIdAndUserAndDeletedFalse(100L, mockUser)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(100L, mockUser));

            verify(taskRepository).findByIdAndUserAndDeletedFalse(100L, mockUser);
        }
    }

    @Nested
    class UpdateTaskTests {
        @Test
        void shouldUpdateTaskWhenExists() {
            Task task = new Task(44L, "title", "desc", false, mockUser, LocalDateTime.now(), LocalDateTime.now(), false);
            TaskRequest taskRequest = new TaskRequest("updated title", "desc", true);

            Task updatedTask = Task.fromDTO(taskRequest);
            updatedTask.setUser(mockUser);
            updatedTask.setId(task.getId());

            when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

            Task result = taskService.updateTask(task.getId(), taskRequest, mockUser);

            assertEquals(result.getId(), updatedTask.getId());
            assertEquals(result.getTitle(), updatedTask.getTitle());
            assertEquals(result.getDescription(), updatedTask.getDescription());
            assertEquals(result.getCompleted(), updatedTask.getCompleted());

            verify(taskRepository).findById(task.getId());
            verify(taskRepository).save(task);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenNotExists() {
            when(taskRepository.findById(1L)).thenReturn(Optional.empty());

            TaskRequest taskRequest = new TaskRequest("title", "desc", false);

            assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(1L, taskRequest, mockUser));

            verify(taskRepository).findById(1L);
            verify(taskRepository, never()).save(any());
        }

        @Test
        void shouldThrowAccessDeniedExceptionWhenNotOwned() {
            var actualOwner = AppUser.builder().id(100L).build();
            var mockTask = new Task(100L, "T", "D", false, actualOwner, LocalDateTime.now(), LocalDateTime.now(), false);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(AccessDeniedException.class, () -> taskService.updateTask(100L, any(), mockUser));

            verify(taskRepository).findById(100L);
            verify(taskRepository, never()).save(any());
        }
    }
}
