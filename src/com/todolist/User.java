package com.todolist;

public class User {
    private int id;
    private String username;
    private String password;

    // Constructor with ID, username, and password
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Constructor with username and password (no ID)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Constructor with ID and username only
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getter and Setter methods
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}


