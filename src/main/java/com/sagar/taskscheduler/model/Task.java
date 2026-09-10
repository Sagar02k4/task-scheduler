package com.sagar.taskscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;

    private String description;

    private String assignee;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate deadline;

    private LocalDate createdAt = LocalDate.now();

    @ManyToMany
    @JoinTable(
        name = "task_dependencies",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "depends_on_id")
    )
    private List<Task> dependencies = new ArrayList<>();
}