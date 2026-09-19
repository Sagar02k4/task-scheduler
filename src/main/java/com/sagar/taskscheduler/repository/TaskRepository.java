package com.sagar.taskscheduler.repository;

import com.sagar.taskscheduler.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerUsername(String username);
    Page<Task> findByOwnerUsername(String username, Pageable pageable);
}
