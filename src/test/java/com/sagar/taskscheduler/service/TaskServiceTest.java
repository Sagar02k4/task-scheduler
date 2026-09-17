package com.sagar.taskscheduler.service;

import com.sagar.taskscheduler.model.Priority;
import com.sagar.taskscheduler.model.Task;
import com.sagar.taskscheduler.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task taskA;
    private Task taskB;
    private Task taskC;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        taskA = new Task();
        taskA.setId(1L);
        taskA.setTitle("Task A");
        taskA.setPriority(Priority.LOW);

        taskB = new Task();
        taskB.setId(2L);
        taskB.setTitle("Task B");
        taskB.setPriority(Priority.HIGH);

        taskC = new Task();
        taskC.setId(3L);
        taskC.setTitle("Task C");
        taskC.setPriority(Priority.MEDIUM);
    }

    // ---------- Cycle Detection Tests ----------

    @Test
    public void testAddDependency_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskA));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(taskB));
        when(taskRepository.save(any(Task.class))).thenReturn(taskA);

        Task result = taskService.addDependency(1L, 2L);

        assertNotNull(result);
        assertTrue(result.getDependencies().contains(taskB));
    }

    @Test
    public void testAddDependency_CreatesCycle_ThrowsException() {
        taskA.getDependencies().add(taskB);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskA));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(taskB));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.addDependency(2L, 1L);
        });

        assertEquals("Adding this dependency creates a cycle", exception.getMessage());
    }

    // ---------- Topological Sort Tests ----------

    @Test
    public void testGetExecutionOrder_SimpleChain() {
        taskB.getDependencies().add(taskA);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(taskA, taskB));

        List<Task> result = taskService.getExecutionOrder();

        assertEquals(2, result.size());
        assertEquals(taskA.getId(), result.get(0).getId());
        assertEquals(taskB.getId(), result.get(1).getId());
    }

    @Test
    public void testGetExecutionOrder_IndependentTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(taskA, taskC));

        List<Task> result = taskService.getExecutionOrder();

        assertEquals(2, result.size());
    }

    // ---------- Priority-based Scheduling Tests ----------

    @Test
    public void testPriorityExecutionOrder_RespectsPriority() {
        // taskA = LOW, taskB = HIGH, dono independent hain
        when(taskRepository.findAll()).thenReturn(Arrays.asList(taskA, taskB));

        List<Task> result = taskService.getPriorityBasedExecutionOrder();

        assertEquals(taskB.getId(), result.get(0).getId()); // HIGH pehle aana chahiye
        assertEquals(taskA.getId(), result.get(1).getId()); // LOW baad mein
    }

    @Test
    public void testPriorityExecutionOrder_DependencyOverridesPriority() {
        // taskC (MEDIUM) depends on taskA (LOW)
        // taskB (HIGH) independent hai
        taskC.getDependencies().add(taskA);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(taskA, taskB, taskC));

        List<Task> result = taskService.getPriorityBasedExecutionOrder();

        // taskC HIGH priority nahi hai, lekin taskA ki dependency hai
        // Toh taskC kabhi taskA se pehle nahi aa sakta, chahe koi priority ho
        int indexOfA = result.indexOf(taskA);
        int indexOfC = result.indexOf(taskC);

        assertTrue(indexOfA < indexOfC, "Task A must come before Task C due to dependency");
    }
}