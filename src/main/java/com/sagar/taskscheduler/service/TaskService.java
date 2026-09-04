package com.sagar.taskscheduler.service;

import com.sagar.taskscheduler.repository.TaskRepository;
import com.sagar.taskscheduler.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Task getTaskById(Long Id){
        return taskRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Cannot find Task by this Id"));
    }

    public void deleteTask(Long Id){
        taskRepository.deleteById(Id);
    }

}
