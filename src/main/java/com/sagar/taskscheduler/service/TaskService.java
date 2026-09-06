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

    public Task addDependency(Long taskId, Long dependsonId){
        Task task = getTaskById(taskId);
        Task dependsonTask = getTaskById(dependsonId);

        if(taskId.equals(dependsonId)) {
            throw new RuntimeException("A task cannot depend on itself");
        }

        if(hasCycle(dependsonTask, taskId)){
            throw new RuntimeException("Adding this dependency creates a cycle");
        }

        task.getDependencies().add(dependsonTask);
        return taskRepository.save(task);
    }

    public boolean hasCycle(Task current , Long targetId) {
        if(current.getId().equals(targetId)){
            return true;
        }
        for(Task dependency : current.getDependencies()){
            if(hasCycle(dependency, targetId)){
                return true;
            }
        }
        return false;
    }

}
