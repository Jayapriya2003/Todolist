package com.todolist;

import java.sql.*;

public class UserManager {
    
    // Method to register a user into the database
    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Registration failed.");
            e.printStackTrace();
            return false;
        }
    }

    // Method to verify the login credentials from the database
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // User found in the database, return User object
                return new User(rs.getInt("id"), rs.getString("username"));
            } else {
                // Invalid credentials
                System.out.println("Invalid credentials.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while verifying credentials.");
            e.printStackTrace();
            return null;
        }
    }
}




