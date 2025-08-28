package com.wipro.librarymanagementsystem.controller;

import com.wipro.librarymanagementsystem.dto.AuthResponse;
import com.wipro.librarymanagementsystem.dto.LoginRequest;
import com.wipro.librarymanagementsystem.entity.Admin;
import com.wipro.librarymanagementsystem.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AdminService adminService;
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth controller is working");
    }
    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<Admin> adminOpt = adminService.authenticateAdmin(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                
                // Create admin info for response (without password)
                AuthResponse.AdminInfo adminInfo = new AuthResponse.AdminInfo(
                    admin.getId(),
                    admin.getUsername(),
                    admin.getFullName(),
                    admin.getEmail(),
                    admin.getRole().toString(),
                    admin.getLastLogin()
                );
                
                AuthResponse response = new AuthResponse(true, "Login successful", adminInfo);
                return ResponseEntity.ok(response);
                
            } else {
                AuthResponse response = new AuthResponse(false, "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            AuthResponse response = new AuthResponse(false, "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
   
    
    // Get current admin profile (requires authentication)
    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile() {
        // For now, return a placeholder
        // In a full JWT implementation, this would extract admin info from the token
        AuthResponse response = new AuthResponse(true, "Profile endpoint - implement JWT for full functionality");
        return ResponseEntity.ok(response);
    }
    
    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        // In a stateless JWT system, logout is handled client-side
        AuthResponse response = new AuthResponse(true, "Logout successful");
        return ResponseEntity.ok(response);
    }
}