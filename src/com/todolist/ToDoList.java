package com.todolist;

import java.io.Console;
import java.util.Scanner;

public class ToDoList {
    private TaskManager taskManager;
    private UserManager userManager;
    private User currentUser;

    // Define ANSI color codes for console text formatting
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    public ToDoList() {
        taskManager = new TaskManager();
        userManager = new UserManager();
    }

    public static void main(String[] args) {
        ToDoList toDoList = new ToDoList();
        Scanner scanner = new Scanner(System.in);
        Console console = System.console(); // Get console for password masking

        System.out.println("Welcome to the To-Do List App!");
        System.out.print("Do you have an account? (yes/no): ");
        String hasAccount = scanner.nextLine();

        if (hasAccount.equalsIgnoreCase("no")) {
            // Registration process
            System.out.print("Enter a username: ");
            String username = scanner.nextLine();

            String password;
            if (console != null) {
                // Masked password entry for secure input
                System.out.print("Enter a password: ");
                char[] passwordChars = console.readPassword();
                password = new String(passwordChars);
            } else {
                // Fallback if console is not available
                System.out.print("Enter a password (input won't be masked): ");
                password = readPassword(scanner); // Use custom password masking
            }

            toDoList.userManager.registerUser(username, password);
            System.out.println(GREEN + "Account created successfully!" + RESET);
        }

        // Login process
        System.out.println("Please log in.");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        String password;
        if (console != null) {
            System.out.print("Password: ");
            char[] passwordChars = console.readPassword();
            password = new String(passwordChars);
        } else {
            System.out.print("Password (input won't be masked): ");
            password = readPassword(scanner); // Use custom password masking
        }

        // Attempt to log in
        toDoList.currentUser = toDoList.userManager.loginUser(username, password);

        if (toDoList.currentUser == null) {
            System.out.println(RED + "Login failed. Exiting application." + RESET);
            scanner.close();
            return;
        }

        System.out.println(GREEN + "Login successful!" + RESET);
        toDoList.taskManager.loadTasksFromDatabase(toDoList.currentUser.getId());
        toDoList.runTaskMenu(scanner);
    }

    private void runTaskMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n1. Add task");
            System.out.println("2. Remove task");
            System.out.println("3. Mark task as completed");
            System.out.println("4. Display all tasks");
            System.out.println("5. Notify upcoming tasks");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addTask(scanner);
                    break;

                case 2:
                    removeTask(scanner);
                    break;

                case 3:
                    markTaskAsCompleted(scanner);
                    break;

                case 4:
                    displayTasks();
                    break;

                case 5:
                    taskManager.loadUpcomingTasksFromDatabase(currentUser.getId());
                    break;


                case 6:
                    System.out.println(GREEN + "Exiting application." + RESET);
                    scanner.close();
                    return;

                default:
                    System.out.println(RED + "Invalid choice. Please try again." + RESET);
                    break;
            }
        }
    }

    private void addTask(Scanner scanner) {
        System.out.print("Enter task name: ");
        String taskName = scanner.nextLine();
        System.out.print("Enter reminder date and time (yyyy-MM-dd HH:mm): ");
        String dateTimeInput = scanner.nextLine();
        System.out.print("Enter due date (yyyy-MM-dd HH:mm): ");
        String dueDateInput = scanner.nextLine();
        System.out.print("Enter place for reminder: ");
        String place = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter priority (High, Medium, Low): ");
        String priority = scanner.nextLine();

        taskManager.addTask(currentUser.getId(), taskName, dateTimeInput, dueDateInput, place, category, priority);
        System.out.println(GREEN + "Task added: " + taskName + RESET);
    }

    private void removeTask(Scanner scanner) {
        System.out.print("Enter task ID to remove: ");
        int removeId = scanner.nextInt();
        taskManager.deleteTaskFromDatabase(removeId);
        System.out.println(BLUE + "Task removed successfully!" + RESET);
    }

    private void markTaskAsCompleted(Scanner scanner) {
        System.out.print("Enter task ID to mark as completed: ");
        int completeId = scanner.nextInt();
        taskManager.markTaskAsCompletedInDatabase(completeId);
        System.out.println(CYAN + "Task marked as completed!" + RESET);
    }

    private void displayTasks() {
        System.out.println(YELLOW + "Displaying all tasks:" + RESET);
        taskManager.loadTasksFromDatabase(currentUser.getId());
        taskManager.displayTasks();
    }

    private static String readPassword(Scanner scanner) {
        StringBuilder password = new StringBuilder();
        while (true) {
            char ch = scanner.next().charAt(0); // Read each character
            if (ch == '\n' || ch == '\r') { // End input on newline
                break;
            }
            password.append(ch); // Append each character to password
            System.out.print("*"); // Print asterisks for each character
        }
        return password.toString();
    }
}





   