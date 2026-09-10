package com.sagar.taskscheduler.service;

import com.sagar.taskscheduler.model.Status;
import com.sagar.taskscheduler.repository.TaskRepository;
import com.sagar.taskscheduler.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<Task> getExecutionOrder(){
        List<Task> allTasks = taskRepository.findAll();
        List<Task> sortedOrder = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for(Task task : allTasks){
            if(!visited.contains(task.getId())){
                topologicalSortUtil(task, visited, sortedOrder);
            }
        }

        return sortedOrder;
    }

    public void topologicalSortUtil(Task task, Set<Long> visited, List<Task> sortedOrder){
        visited.add(task.getId());

        for(Task dependency : task.getDependencies()){
            if(!visited.contains(dependency.getId())){
                topologicalSortUtil(dependency, visited, sortedOrder);
            }
        }

        sortedOrder.add(task);
    }

    public List<Task> getPriorityBasedRxrcutionOrder() {
        List<Task> allTasks = taskRepository.findAll();

        Map<Long, Integer> inDegree = new HashMap<>();
        Map<Long, List<Task>> dependents = new HashMap<>();

        for(Task task : allTasks){
            inDegree.put(task.getId(), task.getDependencies().size());
            dependents.putIfAbsent(task.getId(), new ArrayList<>());
        }

        for(Task task : allTasks){
            for(Task dependency : task.getDependencies()){
                dependents.get(dependency.getId()).add(task);
            }
        }

        PriorityQueue<Task> readyQueue = new PriorityQueue<>( (a,b) -> b.getPriority().ordinal() - a.getPriority().ordinal());

        for(Task task : allTasks){
            if(inDegree.get(task.getId()) == 0){
                readyQueue.add(task);
            }
        }

        List<Task> result = new ArrayList<>();

        while(!readyQueue.isEmpty()){
            Task current = readyQueue.poll();
            result.add(current);
            for(Task dependent : dependents.get(current.getId())){
                int newInDegree = inDegree.get(dependent.getId()) - 1;
                inDegree.put(dependent.getId(), newInDegree);

                if(newInDegree == 0){
                    readyQueue.add(dependent);
                }
            }
        }

        return result;
    }

    public List<String> detectDeadlineConflict(){
        List<Task> alltasks = taskRepository.findAll();
        List<String> conflicts = new ArrayList<>();
        Map<String, List<Task>> tasksbyAssigneeAndDate = new HashMap<>();

        for(Task task : alltasks){
            if(task.getAssignee() == null || task.getDeadline() == null){
                continue;
            }
            String key = task.getAssignee() + "_" + task.getDeadline();
            tasksbyAssigneeAndDate.putIfAbsent(key,new ArrayList<>());
            tasksbyAssigneeAndDate.get(key).add(task);
        }

        for(Map.Entry<String, List<Task>> entry : tasksbyAssigneeAndDate.entrySet()){
            List<Task> tasksOnSameDay = entry.getValue();

            if(tasksOnSameDay.size() > 1){
                StringBuilder message = new StringBuilder();
                message.append(tasksOnSameDay.get(0).getAssignee())
                        .append(" has ")
                        .append(tasksOnSameDay.size())
                        .append(" tasks due on ")
                        .append(tasksOnSameDay.get(0).getDeadline())
                        .append(": ");

                for(Task t : tasksOnSameDay){
                    message.append(t.getTitle()).append(", ");
                }

                conflicts.add(message.toString());
            }
        }
        return conflicts;
    }

    public Task updateStatus(Long taskId, Status newStatus){
        Task task = getTaskById(taskId);

        if(newStatus == Status.IN_PROGRESS || newStatus == Status.DONE){
            for(Task dependency : task.getDependencies()){
                if(dependency.getStatus() != Status.DONE){
                    throw new RuntimeException("Cannot start this task. Dependency " + dependency.getTitle() + " is not completed yet.");
                }
            }
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

}
