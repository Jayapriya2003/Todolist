package com.todolist;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskManager {
    private ArrayList<Task> tasks = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Add a task to the list and the database
    public void addTask(int userId, String taskName, String reminderDateTimeInput, String dueDateInput, String place, String category, String priority) {
        LocalDateTime reminderDateTime = LocalDateTime.parse(reminderDateTimeInput, formatter);
        LocalDateTime dueDate = LocalDateTime.parse(dueDateInput, formatter);
        Task newTask = new Task(userId, taskName, reminderDateTime, dueDate, place, category, priority);
        tasks.add(newTask);
        addTaskToDatabase(newTask);
    }

    // Save task to the database
    public void addTaskToDatabase(Task task) {
        String sql = "INSERT INTO task (user_id, taskName, isCompleted, reminderDateTime, due_date, place, category, priority) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, task.getUserId());
            pstmt.setString(2, task.getTaskName());
            pstmt.setBoolean(3, task.isCompleted());
            pstmt.setTimestamp(4, Timestamp.valueOf(task.getReminderDateTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(task.getDueDate())); // Add due date
            pstmt.setString(6, task.getPlace());
            pstmt.setString(7, task.getCategory());
            pstmt.setString(8, task.getPriority()); // Add priority
            pstmt.executeUpdate();
            System.out.println("Task added: " + task.getTaskName());
        } catch (SQLException e) {
            System.out.println("Error while adding the task to the database.");
            e.printStackTrace();
        }
    }

    // Load tasks from the database for a specific user
    public void loadTasksFromDatabase(int userId) {
        String sql = "SELECT id, taskName, isCompleted, reminderDateTime, due_date, place, category, priority FROM task WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            tasks.clear();
            while (rs.next()) {
                LocalDateTime reminderDateTime = null;
                LocalDateTime dueDate = null;

                // Check for null before converting to LocalDateTime
                if (rs.getTimestamp("reminderDateTime") != null) {
                    reminderDateTime = rs.getTimestamp("reminderDateTime").toLocalDateTime();
                }

                if (rs.getTimestamp("due_date") != null) {
                    dueDate = rs.getTimestamp("due_date").toLocalDateTime();
                }

                Task task = new Task(
                    userId,
                    rs.getString("taskName"),
                    reminderDateTime,
                    dueDate, // Load due date
                    rs.getString("place"),
                    rs.getString("category"),
                    rs.getString("priority") // Load priority
                );
                if (rs.getBoolean("isCompleted")) {
                    task.markAsCompleted();
                }
                tasks.add(task);
            }
            System.out.println("Tasks loaded from database for user ID: " + userId);
        } catch (SQLException e) {
            System.out.println("Error while loading tasks from the database.");
            e.printStackTrace();
        }
    }

    // Notify about upcoming tasks
    public void notifyUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        boolean taskFound = false;
        
        for (Task task : tasks) {
            if (!task.isCompleted() && task.getDueDate() != null && task.getDueDate().isBefore(now.plusDays(1))) {
                System.out.println("Upcoming Task: " + task.getTaskName() + " (Due: " + task.getDueDate().format(formatter) + ")");
                taskFound = true;
            }
        }
        
        if (!taskFound) {
            System.out.println("No upcoming tasks within the next day.");
        }
    }

    public void loadUpcomingTasksFromDatabase(int userId) {
    	String sql = "SELECT taskName, reminderDateTime, due_date " +
                "FROM task " +
                "WHERE user_id = ? " +
                "AND isCompleted = FALSE " +
                "AND due_date IS NOT NULL " +
                "AND due_date BETWEEN NOW() AND NOW() + INTERVAL 1 DAY";
          try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            boolean taskFound = false; // To check if there are upcoming tasks
          
            System.out.println("Upcoming tasks within the next day:");
            while (rs.next()) {
                taskFound = true;
                String taskName = rs.getString("taskName");
                LocalDateTime reminderDateTime = (rs.getTimestamp("reminderDateTime") != null) ? 
                                                  rs.getTimestamp("reminderDateTime").toLocalDateTime() : null;
                LocalDateTime dueDate = (rs.getTimestamp("due_date") != null) ? 
                                        rs.getTimestamp("due_date").toLocalDateTime() : null;

                // Print task details
                System.out.println("Task Name: " + taskName);
                System.out.println("Reminder: " + (reminderDateTime != null ? reminderDateTime.format(formatter) : "None"));
                System.out.println("Due Date: " + (dueDate != null ? dueDate.format(formatter) : "None"));
                System.out.println(); // Blank line for spacing between tasks
            }

            if (!taskFound) {
                System.out.println("No upcoming tasks within the next day.");
            }

        } catch (SQLException e) {
            System.out.println("Error while loading upcoming tasks from the database.");
            e.printStackTrace();
        }
    }

    // Display all tasks
    public void displayTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available");
            return;
        }
        for (Task task : tasks) {
            System.out.println("\u001B[33m" + "Task: " + task.getTaskName() + "\u001B[0m");
            System.out.println("Priority: " + task.getPriority());
            if (task.getDueDate() != null) {
                System.out.println("Due Date: " + task.getDueDate().format(formatter));
            }
            System.out.println("Completed: " + (task.isCompleted() ? "Yes" : "No"));
            if (task.getReminderDateTime() != null) {
                System.out.println("Reminder: " + task.getReminderDateTime().format(formatter));
            }
            System.out.println("Place: " + task.getPlace());
            System.out.println("Category: " + task.getCategory());
            System.out.println();
        }
    }

    // Display tasks in a calendar format
    public void displayTasksInCalendarFormat() {
        // Check if tasks list is empty
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        // Group tasks by date
        Map<LocalDate, List<Task>> tasksByDate = tasks.stream()
            .collect(Collectors.groupingBy(task -> task.getReminderDateTime().toLocalDate()));

        // Display tasks grouped by date
        for (LocalDate date : tasksByDate.keySet()) {
            System.out.println("\nTasks for " + date + ":");
            for (Task task : tasksByDate.get(date)) {
                System.out.println("  - " + task);
            }
        }
    }

    // Mark a task as completed in the database
    public void markTaskAsCompletedInDatabase(int taskId) {
        String sql = "UPDATE task SET isCompleted = TRUE WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            System.out.println("Task marked as completed.");
        } catch (SQLException e) {
            System.out.println("Error while marking task as completed.");
            e.printStackTrace();
        }
    }

    // Delete a task from the database
    public void deleteTaskFromDatabase(int taskId) {
        String sql = "DELETE FROM task WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            System.out.println("Task deleted from database.");
        } catch (SQLException e) {
            System.out.println("Error while deleting the task from the database.");
            e.printStackTrace();
        }
    }
}







