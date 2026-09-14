package com.sagar.taskscheduler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, message = "Title name should be atleast 3 characters")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    private String description;

    private String assignee;

    @NotNull(message = "Priority must be specified")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @FutureOrPresent(message = "Deadline cannot be in the past")
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