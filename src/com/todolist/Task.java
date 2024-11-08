package com.todolist;

import java.time.LocalDateTime;

public class Task {
    private int userId;                   // To store the ID of the user who created the task
    private String taskName;              // Task name
    private LocalDateTime reminderDateTime; // Date and time of the reminder
    private LocalDateTime dueDate;        // New field for due date
    private String place;                 // Place of the reminder
    private String category;              // Category of the task
    private String priority;               // New field for task priority
    private boolean isCompleted;          // Track if the task is completed

    // Constructor with user ID, due date, and priority
    public Task(int userId, String taskName, LocalDateTime reminderDateTime, LocalDateTime dueDate, String place, String category, String priority) {
        this.userId = userId;
        this.taskName = taskName;
        this.reminderDateTime = reminderDateTime;
        this.dueDate = dueDate;            // Initialize due date
        this.place = place;
        this.category = category;
        this.priority = priority;          // Initialize priority
        this.isCompleted = false;         // Default to not completed
    }

    // Overloaded constructor without user ID (for general use)
    public Task(String taskName, LocalDateTime reminderDateTime, LocalDateTime dueDate, String place, String category, String priority) {
        this.taskName = taskName;
        this.reminderDateTime = reminderDateTime;
        this.dueDate = dueDate;            // Initialize due date
        this.place = place;
        this.category = category;
        this.priority = priority;          // Initialize priority
        this.isCompleted = false;         // Default to not completed
    }

    // Getters
    public int getUserId() { return userId; }
    public String getTaskName() { return taskName; }
    public LocalDateTime getReminderDateTime() { return reminderDateTime; }
    public LocalDateTime getDueDate() { return dueDate; }
    public String getPlace() { return place; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return isCompleted; }

    // Mark as completed
    public void markAsCompleted() { this.isCompleted = true; }

    // Convert task to a string format for file or database saving
    public String toFileString() {
        return userId + "," + taskName + "," + isCompleted + "," + reminderDateTime + "," + dueDate + "," + place + "," + category + "," + priority;
    }

    // Re-create a task from a saved string (useful for file-based storage)
    public static Task fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        int userId = Integer.parseInt(parts[0]);
        String taskName = parts[1];
        boolean isCompleted = Boolean.parseBoolean(parts[2]);
        LocalDateTime reminderDateTime = LocalDateTime.parse(parts[3]);
        LocalDateTime dueDate = LocalDateTime.parse(parts[4]); // Load due date
        String place = parts[5];
        String category = parts[6];
        String priority = parts[7]; // Load priority

        Task task = new Task(userId, taskName, reminderDateTime, dueDate, place, category, priority);
        if (isCompleted) {
            task.markAsCompleted();
        }
        return task;
    }

    @Override
    public String toString() {
        return "Task: " + taskName + (isCompleted ? " (Completed)" : " (Pending)") +
               ", Reminder: " + reminderDateTime +
               ", Due: " + dueDate +
               ", Place: " + place +
               ", Category: " + category +
               ", Priority: " + priority;
    }
}







