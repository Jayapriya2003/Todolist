package com.todolist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    // Define the database URL, user, and password
    private static final String URL = "jdbc:mysql://localhost:3306/todo_list"; // Use your actual database name
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = "jayapriya2003"; // Replace with your actual password

    // Method to establish a database connection
    public static Connection connect() {
        try {
            // Try connecting to the database and return the connection object
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // In case of an exception, print the error message and stack trace
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
            return null; // Return null if the connection fails
        }
    }
}
