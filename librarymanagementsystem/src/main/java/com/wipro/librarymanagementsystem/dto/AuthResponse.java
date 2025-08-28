package com.wipro.librarymanagementsystem.dto;

import java.time.LocalDateTime;

public class AuthResponse {
    
    private boolean success;
    private String message;
    private String token; 
    private AdminInfo admin;
    private LocalDateTime timestamp;
    
    // Inner class for admin info (without password)
    public static class AdminInfo {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String role;
        private LocalDateTime lastLogin;
        
        // Constructors
        public AdminInfo() {}
        
        public AdminInfo(Long id, String username, String fullName, String email, String role, LocalDateTime lastLogin) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.lastLogin = lastLogin;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public LocalDateTime getLastLogin() { return lastLogin; }
        public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    }
    
    // Constructors
    public AuthResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AuthResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public AuthResponse(boolean success, String message, AdminInfo admin) {
        this(success, message);
        this.admin = admin;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public AdminInfo getAdmin() { return admin; }
    public void setAdmin(AdminInfo admin) { this.admin = admin; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}