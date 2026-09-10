package com.sagar.taskscheduler.controller;

import com.sagar.taskscheduler.model.Status;
import com.sagar.taskscheduler.model.Task;
import com.sagar.taskscheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/{taskId}/dependencies/{dependsOnId}")
    public Task addDependency(@PathVariable Long taskId, @PathVariable Long dependsOnId){
        return taskService.addDependency(taskId, dependsOnId);
    }

    @GetMapping("/execution-order")
    public List<Task> getExecutionOrder(){
        return taskService.getExecutionOrder();
    }

    @GetMapping("/priority-execution-order")
    public List<Task> getPriorityBasedExecutionOrder(){
        return taskService.getPriorityBasedRxrcutionOrder();
    }


    @GetMapping("/deadline-conflicts")
    public List<String> getDeadlineConflicts(){
        return taskService.detectDeadlineConflict();
    }

    @PatchMapping("/{id}/status")
    public Task updateStatus(@PathVariable Long id, @RequestParam Status status){
        return taskService.updateStatus(id, status);
    }
}