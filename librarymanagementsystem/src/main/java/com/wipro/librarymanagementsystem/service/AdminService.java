package com.wipro.librarymanagementsystem.service;

import com.wipro.librarymanagementsystem.entity.Admin;
import com.wipro.librarymanagementsystem.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Get all admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    // Get admin by ID
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }
    
    // Get admin by username
    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
    
    // Create admin with encrypted password
    public Admin createAdmin(Admin admin) {
        // Check if username already exists
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Username '" + admin.getUsername() + "' already exists");
        }
        
        // Check if email already exists
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Email '" + admin.getEmail() + "' already exists");
        }
        
        // Encrypt password
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        
        return adminRepository.save(admin);
    }
    
    // Authenticate admin
    public Optional<Admin> authenticateAdmin(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsernameAndIsActiveTrue(username);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                // Update last login time
                admin.setLastLogin(LocalDateTime.now());
                adminRepository.save(admin);
                return Optional.of(admin);
            }
        }
        
        return Optional.empty();
    }
    
    // Update admin
    public Admin updateAdmin(Long id, Admin adminDetails) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        
        // Check if username is being changed and if new username exists
        if (!admin.getUsername().equals(adminDetails.getUsername()) && 
            adminRepository.existsByUsername(adminDetails.getUsername())) {
            throw new RuntimeException("Username '" + adminDetails.getUsername() + "' already exists");
        }
        
        // Check if email is being changed and if new email exists
        if (!admin.getEmail().equals(adminDetails.getEmail()) && 
            adminRepository.existsByEmail(adminDetails.getEmail())) {
            throw new RuntimeException("Email '" + adminDetails.getEmail() + "' already exists");
        }
        
        admin.setUsername(adminDetails.getUsername());
        admin.setFullName(adminDetails.getFullName());
        admin.setEmail(adminDetails.getEmail());
        admin.setRole(adminDetails.getRole());
        admin.setIsActive(adminDetails.getIsActive());
        
        // Only update password if provided and not empty
        if (adminDetails.getPassword() != null && !adminDetails.getPassword().trim().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
        }
        
        return adminRepository.save(admin);
    }
    
    // Change password
    public Admin changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update with new password
        admin.setPassword(passwordEncoder.encode(newPassword));
        return adminRepository.save(admin);
    }
    
    // Deactivate admin
    public Admin deactivateAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        admin.setIsActive(false);
        return adminRepository.save(admin);
    }
    
    // Activate admin
    public Admin activateAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        admin.setIsActive(true);
        return adminRepository.save(admin);
    }
    
    // Get active admins
    public List<Admin> getActiveAdmins() {
        return adminRepository.findByIsActiveTrue();
    }
    
    // Initialize default admin (call this on application startup)
    public void initializeDefaultAdmin() {
        if (adminRepository.count() == 0) {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword("admin123"); // Will be encrypted
            defaultAdmin.setFullName("System Administrator");
            defaultAdmin.setEmail("admin@library.com");
            defaultAdmin.setRole(Admin.AdminRole.SUPER_ADMIN);
            
            createAdmin(defaultAdmin);
            System.out.println("Default admin created - Username: admin, Password: admin123");
        }
    }
}