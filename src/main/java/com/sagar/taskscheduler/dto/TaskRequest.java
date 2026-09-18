package com.sagar.taskscheduler.dto;

import com.sagar.taskscheduler.model.Priority;
import com.sagar.taskscheduler.model.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    private String description;

    @NotNull(message = "Priority must be specified")
    private Priority priority;

    private Status status;

    @FutureOrPresent(message = "Deadline cannot be in the past")
    private LocalDate deadline;

    private String assignee;
}